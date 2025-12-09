package com.sgave.mall.sgavemallinventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemallinventory.constant.InventoryChangeType;
import com.sgave.mall.sgavemallinventory.dto.*;
import com.sgave.mall.sgavemallinventory.mapper.InventoryLogMapper;
import com.sgave.mall.sgavemallinventory.mapper.InventoryMapper;
import com.sgave.mall.sgavemallinventory.pojo.Goods;
import com.sgave.mall.sgavemallinventory.pojo.Inventory;
import com.sgave.mall.sgavemallinventory.pojo.InventoryLog;
import com.sgave.mall.sgavemallinventory.remote.facade.GoodRemoteFacade;
import com.sgave.mall.sgavemallinventory.service.AdminInventoryService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@Service
public class AdminInventoryServiceImpl implements AdminInventoryService {

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private InventoryLogMapper inventoryLogMapper;

    @Resource
    private GoodRemoteFacade goodRemoteFacade;


    @Override
    public IPage<Inventory> getInventoryList(Integer page, Integer limit, String name, String goodsSn, Byte minStatus, String sortField, String sortOrder) {
        Page<Inventory> pageInfo = new Page<>(page, limit);
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();

        if (minStatus != null && minStatus == 1) {
            // 可用库存 <= 预警库存
            queryWrapper.apply("available_quantity <= min_stock");
        } else if (minStatus != null && minStatus == 0) {
            // 可用库存 > 预警库存
            queryWrapper.apply("available_quantity > min_stock");
        }
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        if (StringUtils.isNotBlank(goodsSn)) {
            queryWrapper.like("goods_sn", goodsSn);
        }
        //排序
        if (StringUtils.isNotBlank(sortField)) {
            boolean asc = "asc".equalsIgnoreCase(sortOrder);
            queryWrapper.orderBy(true, asc, sortField);
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc("create_time");
        }
        queryWrapper.eq("is_deleted", false);
        //分页查询
        return inventoryMapper.selectPage(pageInfo, queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean lockBatch(List<InventoryLockDTO> inventoryLockDTOList) {
        ArrayList<InventoryLog> inventoryLogList = new ArrayList<>();
        for (InventoryLockDTO req : inventoryLockDTOList) {
            // 查询库存
            Inventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>().eq(Inventory::getGoodsSn, req.getGoodsSn()));
            if (inventory == null) {
                throw new RuntimeException("商品不存在：" + req.getGoodsSn());
            }
            if (inventory.getAvailableQuantity() < req.getQuantity()) {
                throw new RuntimeException("库存不足：" + req.getGoodsSn());
            }
            // 保存旧版本号
            Integer oldVersion = inventory.getVersion();
            // 修改库存数量
            int newAvailable = inventory.getAvailableQuantity() - req.getQuantity();
            int newLocked = inventory.getLockedQuantity() + req.getQuantity();
            if (inventory.getMinStock() != null && newAvailable <= inventory.getMinStock()) {
                inventory.setMinStatus((byte) 1);  // 预警
            }
            // 使用乐观锁更新库存
            int updateCount = inventoryMapper.update(
                    null,
                    new LambdaUpdateWrapper<Inventory>()
                            .eq(Inventory::getId, inventory.getId())
                            .eq(Inventory::getVersion, oldVersion)
                            .set(Inventory::getAvailableQuantity, newAvailable)
                            .set(Inventory::getLockedQuantity, newLocked)
                            .set(Inventory::getVersion, oldVersion + 1)
                            .set(inventory.getMinStock() != null && newAvailable <= inventory.getMinStock(), Inventory::getMinStatus, (byte) 1)
            );
            // 如果 updateCount == 0，说明 version 不一致，发生并发冲突
            if (updateCount == 0) {
                throw new RuntimeException("库存更新失败（乐观锁冲突）：" + req.getGoodsSn());
            }

            // 写日志
            InventoryLog log = new InventoryLog();
            log.setGoodsSn(inventory.getGoodsSn());
            log.setGoodsName(inventory.getName());
            log.setChangeType(InventoryChangeType.LOCK);
            log.setQuantity(req.getQuantity());
            if (req.getOrderNo() != null) {
                log.setOrderNo(req.getOrderNo());
            }
            log.setCreateTime(LocalDateTime.now());
            inventoryLogList.add(log);
        }
        inventoryLogMapper.insert(inventoryLogList);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlockBatch(List<InventoryLockDTO> inventoryLockDTOList) {
        ArrayList<InventoryLog> inventoryLogList = new ArrayList<>();
        for (InventoryLockDTO req : inventoryLockDTOList) {
            // 查询库存
            Inventory inventory = inventoryMapper.selectOne(new QueryWrapper<Inventory>().eq("goods_sn", req.getGoodsSn()));

            if (inventory == null || inventory.getLockedQuantity() == null) continue;
            // 更新库存数量
            int locked = inventory.getLockedQuantity();
            int reqQty = req.getQuantity();
            // 实际可操作的数量（不能超过 locked）
            int realQty = Math.min(locked, reqQty);
            if (realQty <= 0) continue;
            Integer oldVersion = inventory.getVersion();

            // 乐观锁更新库存
            int updateCount = inventoryMapper.update(
                    null,
                    new LambdaUpdateWrapper<Inventory>()
                            .eq(Inventory::getId, inventory.getId())
                            .eq(Inventory::getVersion, oldVersion) // 乐观锁条件
                            .set(Inventory::getAvailableQuantity, inventory.getAvailableQuantity() + realQty)
                            .set(Inventory::getLockedQuantity, locked - realQty)
                            .set(Inventory::getVersion, oldVersion + 1)
            );
            if (updateCount == 0) {
                throw new RuntimeException("库存解锁失败（乐观锁冲突）：" + req.getGoodsSn());
            }

            // 写日志
            InventoryLog log = new InventoryLog();
            log.setGoodsSn(inventory.getGoodsSn());
            log.setGoodsName(inventory.getName());
            log.setChangeType(InventoryChangeType.UNLOCK);
            log.setQuantity(realQty);
            if (req.getOrderNo() != null) {
                log.setOrderNo(req.getOrderNo());
            }
            log.setCreateTime(LocalDateTime.now());
            inventoryLogList.add(log);
        }
        inventoryLogMapper.insert(inventoryLogList);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setMinStock(String goodsSn, Integer minStock) {
        //校验参数
        if (goodsSn == null) {
            throw new IllegalArgumentException("商品编号和预警线不能为空");
        }

        //查询对应商品库存
        Inventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getGoodsSn, goodsSn)
        );
        if (inventory == null) {
            throw new RuntimeException("商品库存不存在");
        }

        //更新库存预警线
        if (inventory.getAvailableQuantity() > inventory.getMinStock()) {
            inventory.setMinStatus((byte) 0);  // 正常
        } else {
            inventory.setMinStatus((byte) 1);  // 预警
        }
        inventory.setMinStock(minStock);
        inventory.setUpdateTime(new Date());
        inventoryMapper.updateById(inventory);

        // 记录日志
        InventoryLog log = new InventoryLog();
        log.setGoodsSn(goodsSn);
        log.setGoodsName(inventory.getName());
        log.setChangeType(InventoryChangeType.SET_MIN_STOCK);
        log.setQuantity(minStock);
        log.setCreateTime(LocalDateTime.now());
        inventoryLogMapper.insert(log);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addInventory(Inventory inventory) {
        // 检查库存是否已存在
        Inventory inventoryExit = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getGoodsSn, inventory.getGoodsSn())
        );

        if (inventoryExit != null) {
            return false;
        }
        // 调用商品服务查询商品
        Goods goods = goodRemoteFacade.selectOne(inventory.getGoodsSn());

        // 新增库存
        Inventory inventoryInsert = new Inventory();
        inventoryInsert.setName(goods.getName());
        inventoryInsert.setGoodsSn(inventory.getGoodsSn());
        inventoryInsert.setTotalQuantity(inventory.getTotalQuantity());
        inventoryInsert.setAvailableQuantity(inventory.getTotalQuantity());
        if (inventory.getLockedQuantity() != 0) {
            inventoryInsert.setLockedQuantity(inventory.getLockedQuantity());
            inventoryInsert.setAvailableQuantity(inventory.getTotalQuantity() - inventory.getLockedQuantity());
        } else {
            inventoryInsert.setLockedQuantity(0);
        }
        inventoryInsert.setMinStock(inventory.getMinStock() != null ? inventory.getMinStock() : 0);
        inventoryInsert.setCreateTime(new Date());
        inventoryInsert.setUpdateTime(new Date());
        inventoryMapper.insert(inventoryInsert);

        // 写日志
        InventoryLog log = new InventoryLog();
        log.setGoodsSn(inventory.getGoodsSn());
        log.setGoodsName(goods.getName());
        log.setChangeType(InventoryChangeType.ADD);
        log.setQuantity(inventory.getTotalQuantity());
        log.setCreateTime(LocalDateTime.now());
        inventoryLogMapper.insert(log);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean replenishInventory(String goodsSn, Integer quantity) {
        // 查询库存
        Inventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getGoodsSn, goodsSn)
        );
        if (inventory == null) {
            throw new RuntimeException("库存不存在，请先新增库存");
        }

        // 更新库存数量
        inventory.setTotalQuantity(inventory.getTotalQuantity() + quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setUpdateTime(new Date());
        inventoryMapper.updateById(inventory);
        // 调用商品服务查询商品
        Goods goods = goodRemoteFacade.selectOne(goodsSn);

        // 记录日志
        InventoryLog log = new InventoryLog();
        log.setGoodsSn(goodsSn);
        log.setGoodsName(goods.getName());
        log.setChangeType(InventoryChangeType.REPLENISH);
        log.setQuantity(quantity);
        log.setCreateTime(LocalDateTime.now());
        inventoryLogMapper.insert(log);
        return true;
    }


    @Override
    public IPage<InventoryLog> getInventoryLogs(String goodsSn, String orderNo, Integer page, Integer limit, String sortField, String sortOrder) {
        Page<InventoryLog> pageInfo = new Page<>(page, limit);
        QueryWrapper<InventoryLog> queryWrapper = new QueryWrapper<>();
        if (goodsSn != null && !goodsSn.isEmpty()) {
            queryWrapper.eq("goods_sn", goodsSn);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.eq("order_sn", orderNo);
        }
        //排序
        if (StringUtils.isNotBlank(sortField)) {
            boolean asc = "asc".equalsIgnoreCase(sortOrder);
            queryWrapper.orderBy(true, asc, sortField);
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc("create_time");
        }
        //分页查询
        return inventoryLogMapper.selectPage(pageInfo, queryWrapper);
    }


    @Override
    public CountDTO count() {
        // 统计数据
        Long totalGoodsCount = inventoryMapper.selectCount(new QueryWrapper<Inventory>().eq("is_deleted", false));
        Long warnGoodsCount = inventoryMapper.selectCount(new QueryWrapper<Inventory>().apply("available_quantity <= min_stock").eq("is_deleted", false));
        CountDTO countDTO = new CountDTO();
        countDTO.setTotalGoodsCount(totalGoodsCount);
        countDTO.setWarnGoodsCount(warnGoodsCount);
        return countDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reduceInventory(List<InventoryLockDTO> inventoryLockDTOList) {
        ArrayList<InventoryLog> inventoryLogList = new ArrayList<>();
        for (InventoryLockDTO req : inventoryLockDTOList) {
            // 查询库存
            Inventory inventory = inventoryMapper.selectOne(new QueryWrapper<Inventory>().eq("goods_sn", req.getGoodsSn()));

            if (inventory == null || inventory.getLockedQuantity() == null) continue;
            Integer oldVersion = inventory.getVersion();

            // 乐观锁更新库存
            int updateCount = inventoryMapper.update(
                    null,
                    new LambdaUpdateWrapper<Inventory>()
                            .eq(Inventory::getId, inventory.getId())
                            .eq(Inventory::getVersion, oldVersion) // 乐观锁条件
                            .set(Inventory::getTotalQuantity, inventory.getTotalQuantity() - req.getQuantity())
                            .set(Inventory::getLockedQuantity, inventory.getLockedQuantity() - req.getQuantity())
                            .set(Inventory::getVersion, oldVersion + 1)
            );
            if (updateCount == 0) {
                throw new RuntimeException("库存扣减失败（乐观锁冲突）：" + req.getGoodsSn());
            }
            // 写日志
            InventoryLog log = new InventoryLog();
            log.setGoodsSn(inventory.getGoodsSn());
            log.setGoodsName(inventory.getName());
            log.setChangeType(InventoryChangeType.SALE);
            log.setQuantity(req.getQuantity());
            log.setOrderNo(req.getOrderNo());
            log.setCreateTime(LocalDateTime.now());
            inventoryLogList.add(log);
        }
        inventoryLogMapper.insert(inventoryLogList);
        return true;
    }

}
