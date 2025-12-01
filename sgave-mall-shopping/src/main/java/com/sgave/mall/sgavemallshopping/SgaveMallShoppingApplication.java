package com.sgave.mall.sgavemallshopping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.sgave.mall.sgavemallshopping.mapper")
public class SgaveMallShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaveMallShoppingApplication.class, args);
    }

}
