package com.sgave.mall.sgavemallorder.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String GOODS_UPDATE_EXCHANGE = "goods.update.exchange";
    public static final String GOODS_UPDATE_QUEUE = "goods.update.queue";
    public static final String GOODS_UPDATE_ROUTING_KEY = "goods.update.key";

    @Bean
    public DirectExchange goodsUpdateExchange() {
        return new DirectExchange(GOODS_UPDATE_EXCHANGE, true, false);
    }

    @Bean
    public Queue goodsUpdateQueue() {
        return new Queue(GOODS_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding goodsUpdateBinding() {
        return BindingBuilder.bind(goodsUpdateQueue())
                .to(goodsUpdateExchange())
                .with(GOODS_UPDATE_ROUTING_KEY);
    }
}
