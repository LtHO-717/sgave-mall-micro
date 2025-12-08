package com.sgave.mall.sgavemallgoods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemallgoods.dto.FileInfo;
import com.sgave.mall.sgavemallgoods.dto.Goods;
import com.sgave.mall.sgavemallgoods.dto.Inventory;
import com.sgave.mall.sgavemallgoods.mapper.FileMapper;
import com.sgave.mall.sgavemallgoods.mapper.GoodsMapper;
import com.sgave.mall.sgavemallgoods.remote.facade.InventoryRemoteFacade;
import com.sgave.mall.sgavemallgoods.repository.GoodsESRepository;
import com.sgave.mall.sgavemallgoods.service.GoodsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private InventoryRemoteFacade inventoryRemoteFacade;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private Environment environment;
    @Resource
    private GoodsESRepository goodsESRepository;


    @Override
    @Transactional(rollbackFor = Exception.class) // 数据库事务：失败则回滚
    public Goods saveGoods(Goods goods) throws IllegalArgumentException {
        if (StringUtils.isBlank(goods.getName())) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (StringUtils.isBlank(goods.getGoodsSn())) {
            throw new IllegalArgumentException("商品编号不能为空");
        }
        if (goods.getPrice() == null) {
            throw new IllegalArgumentException("商品价格不能为空");
        }
        //1 先保存商品到数据库
        goodsMapper.insert(goods);
        log.info("新增商品到数据库成功：{}", goods.getId());
        // 2. 同步到 ES（数据库写入成功后，再写 ES）
        goodsESRepository.save(goods);
        log.info("同步商品到 ES 成功：{}", goods.getId());
        //3. 保存商品后，更新Redis缓存
        redisTemplate.opsForValue().set("goods:" + goods.getId(),
                goods, 60, TimeUnit.MINUTES);
        return goods;
    }

    @Override
    public IPage<Goods> getGoodsList(Integer goodsId, String goodsSn, String name, Integer current, Integer size) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(goodsSn)) {
            queryWrapper.like(Goods::getGoodsSn, goodsSn);
        }
        if (!StringUtils.isBlank(name)) {
            queryWrapper.like(Goods::getName, name);
        }
        if (goodsId != null) {
            queryWrapper.eq(Goods::getId, goodsId);
        }
        queryWrapper.orderByDesc(Goods::getCreateTime);
        // 查询所有
        List<Goods> allGoods = goodsMapper.selectList(queryWrapper);

        // 过滤库存
        List<Goods> filtered = allGoods.stream()
                .filter(g -> {
                    Inventory inv = inventoryRemoteFacade.selectOne(g.getGoodsSn());
                    return inv != null && inv.getAvailableQuantity() != null;
                })
                .toList();

        // 手动分页
        int start = (current - 1) * size;
        int end = Math.min(start + size, filtered.size());
        List<Goods> pageRecords = start >= filtered.size() ? new ArrayList<>() : filtered.subList(start, end);

        // 封装结果
        Page<Goods> result = new Page<>(current, size);
        result.setTotal(filtered.size()); // 这里是过滤后的真实总数
        result.setRecords(pageRecords);
        return result;
    }

    @Override
    public int updateGoods(Goods goods) {
        // 1. 更新 MySQL
        int result = goodsMapper.updateById(goods);
        if (result > 0) {
            log.info("新增更新到数据库成功：{}", goods.getId());
            // 2. 同步更新 ES（save 方法：ID 存在则更新，不存在则新增）
            goodsESRepository.save(goods);
            log.info("同步更新商品到 ES 成功：{}", goods.getId());
            // 3. 更新商品后，更新Redis缓存
            redisTemplate.opsForValue().set("goods:" + goods.getId(), goods, 60, TimeUnit.MINUTES);
        }
        return result;

    }

    @Override
    public int updateGoodsShelf(Integer goodsId, Integer status) {
        LambdaUpdateWrapper<Goods> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Goods::getId, goodsId)
                .set(Goods::getStatus, status);
        return goodsMapper.update(lambdaUpdateWrapper);
    }

    private final String UPLOAD_PATH = "D:/files/mall/";

    @Override
    public Object uploadPic(MultipartFile file) {
        /**
         * 将图片存在本地服务器，将图片的访问地址等信息存在数据库
         */
        FileInfo fileInfo = new FileInfo();
        try {
            File dir = new File(UPLOAD_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            Path filePath = Paths.get(UPLOAD_PATH, fileName);
            //StandardCopyOption.REPLACE_EXISTING,是一个用于文件复制操作的选项,
            // 如果目标路径中已经存在文件，使用这个选项可以覆盖该文件
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            //将图片url等信息存数据库
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setFileKey(fileName);
            String host = InetAddress.getLocalHost().getHostAddress();
            // ⚡ 动态获取 application.yml 配置的端口（如 9005）
            String port = environment.getProperty("local.server.port");
            // 返回 localhost 访问
            String url = "http://" + host + ":" + port + "/mall/" + fileName;
            fileInfo.setUrl(url);
//            fileInfo.setUrl(filePath.toUri().getPath());
            fileInfo.setType(file.getContentType());
            fileInfo.setSize((int) file.getSize());
            fileMapper.insert(fileInfo);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return e.getMessage();
        }
        return fileInfo;
    }

    public String generateUniqueFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random = new Random().nextInt(1000);
        return timestamp + "_" + random + extension;
    }

    /**
     * 全量同步：MySQL 所有商品 → ES（覆盖/新增 ES 数据）
     */
    @Override
    public Object syncAllGoodsToEs(Integer current, Integer size) {
// 1. 读取 MySQL 所有商品数据（分页查询，避免大数据量内存溢出）
        Page<Goods> page = new Page<>(current, size);
        IPage<Goods> goodsIPage = goodsMapper.selectPage(page, null);
        log.info("开始全量同步 MySQL 商品到 ES，总数据量：{}", goodsIPage.getSize());
        List<Goods> goodsList = goodsIPage.getRecords();
        // 2. 批量写入 ES（saveAll 自动覆盖已有 ID 的数据）
        try {
            goodsESRepository.saveAll(goodsList);
            log.info("同步第 {} 页数据到 ES 成功，条数：{}", goodsIPage.getCurrent(), goodsList.size());
        } catch (Exception e) {
            log.error("同步第 {} 页数据到 ES 失败，原因：{}", goodsIPage.getCurrent(), e.getMessage());
            // 可选：记录失败数据 ID，后续单独重试
            goodsList.forEach(p -> log.error("失败商品 ID：{}", p.getId()));
        }
        return "总共 "+goodsIPage.getTotal()+"条，"+goodsIPage.getPages()+" 页"+
                "当前 "+goodsIPage.getCurrent()+"页";
    }
}
