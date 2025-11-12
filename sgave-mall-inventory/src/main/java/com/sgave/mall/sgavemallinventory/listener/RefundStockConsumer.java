package com.sgave.mall.sgavemallinventory.listener;

import com.sgave.mall.sgavemallinventory.dto.RefundStockItemDTO;
import com.sgave.mall.sgavemallinventory.dto.RefundStockMessage;
import com.sgave.mall.sgavemallinventory.mapper.InventoryMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@RocketMQMessageListener(topic = "refund-stock-topic", consumerGroup = "refund-stock-consumer-group")
public class RefundStockConsumer implements RocketMQListener<RefundStockMessage> {

    @Resource
    private InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public void onMessage(RefundStockMessage message) {
        System.out.println("收到退款库存回补消息: " + message);

        for (RefundStockItemDTO item : message.getItems()) {
            Long productId = item.getProductId();
            Integer number = item.getQuantity();

            // 更新库存
            int rows = inventoryMapper.addStock(productId, number);
            if (rows == 0) {
                throw new RuntimeException("库存回补失败，productId=" + productId);
            }
        }
    }
}
