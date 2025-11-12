package com.sgave.mall.sgavemalluser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@MapperScan("com.sgave.mall.sgavemalluser.mapper")
@EnableFeignClients(basePackages = "com.sgave.mall.sgavemalluser.remote")
public class SgaveMallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaveMallUserApplication.class, args);
    }

}
