package com.sgave.mall.sgavemallgoods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.sgave.mall.sgavemallgoods.mapper")
@EnableFeignClients(basePackages = "com.sgave.mall.sgavemallgoods.remote")
public class SgaveMallGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaveMallGoodsApplication.class, args);
    }

}
