package com.sgave.mall.sgavemallorder.listener;

import com.sgave.mall.sgavemallorder.config.RabbitMQConfig;
import com.sgave.mall.sgavemallorder.dto.InventoryLockDTO;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RefundInventoryProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送商品更新消息
     */
    public void sendRefundStockMessage(List<InventoryLockDTO> inventoryLockDTOS) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REFUND_INVENTORY_EXCHANGE,
                RabbitMQConfig.REFUND_INVENTORY_ROUTING_KEY,
                inventoryLockDTOS
        );
    }
}
