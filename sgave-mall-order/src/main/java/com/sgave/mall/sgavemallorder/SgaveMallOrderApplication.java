package com.sgave.mall.sgavemallorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.sgave.mall.sgavemallorder.mapper")
@EnableFeignClients(basePackages = "com.sgave.mall.sgavemallorder.remote")
public class SgaveMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaveMallOrderApplication.class, args);
    }

}
