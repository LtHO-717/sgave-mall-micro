package com.sgave.mall.sgavemallinventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 退款后库存增加的交换机、队列、RoutingKey
    public static final String REFUND_INVENTORY_EXCHANGE = "refund.inventory.exchange";
    public static final String REFUND_INVENTORY_QUEUE = "refund.inventory.queue";
    public static final String REFUND_INVENTORY_ROUTING_KEY = "refund.inventory.key";

    // 声明交换机
    @Bean
    public DirectExchange refundStockExchange() {
        return new DirectExchange(REFUND_INVENTORY_EXCHANGE, true, false);
    }

    // 声明队列
    @Bean
    public Queue refundStockQueue() {
        return new Queue(REFUND_INVENTORY_QUEUE, true);
    }

    // 绑定队列和交换机
    @Bean
    public Binding refundStockBinding() {
        return BindingBuilder.bind(refundStockQueue())
                .to(refundStockExchange())
                .with(REFUND_INVENTORY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

