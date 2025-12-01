package com.sgave.mall.sgavemallinventory.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sgave.mall.sgavemallinventory.config.RabbitMQConfig;
import com.sgave.mall.sgavemallinventory.constant.InventoryChangeType;
import com.sgave.mall.sgavemallinventory.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallinventory.mapper.InventoryLogMapper;
import com.sgave.mall.sgavemallinventory.mapper.InventoryMapper;
import com.sgave.mall.sgavemallinventory.pojo.Inventory;
import com.sgave.mall.sgavemallinventory.pojo.InventoryLog;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class RefundInventoryConsumer {

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private InventoryLogMapper inventoryLogMapper;

    @RabbitListener(queues = RabbitMQConfig.REFUND_INVENTORY_QUEUE)
    public void onMessage(List<InventoryLockDTO> message) {
        System.out.println("Consumer收到消息：" + message);
        ArrayList<Inventory> inventoryList = new ArrayList<>();
        ArrayList<InventoryLog> inventoryLogList = new ArrayList<>();
        for (InventoryLockDTO req : message) {
            // 查询库存
            Inventory inventory = inventoryMapper.selectOne(new QueryWrapper<Inventory>().eq("goods_sn", req.getGoodsSn()));

            if (inventory == null || inventory.getLockedQuantity() == null) continue;
            // 更新库存数量

            inventory.setTotalQuantity(inventory.getTotalQuantity() + req.getQuantity());
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + req.getQuantity());
            inventoryList.add(inventory);

            // 写日志
            InventoryLog log = new InventoryLog();
            log.setGoodsSn(inventory.getGoodsSn());
            log.setGoodsName(inventory.getName());
            log.setChangeType(InventoryChangeType.REFUND);
            log.setQuantity(req.getQuantity());
            log.setOrderNo(req.getOrderNo());
            log.setCreateTime(LocalDateTime.now());
            inventoryLogList.add(log);
        }
        inventoryMapper.updateById(inventoryList);
        inventoryLogMapper.insert(inventoryLogList);
    }
}

