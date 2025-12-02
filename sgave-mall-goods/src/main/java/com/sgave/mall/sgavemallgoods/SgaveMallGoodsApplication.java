package com.sgave.mall.sgavemallgoods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.sgave.mall.sgavemallgoods.mapper")
public class SgaveMallGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaveMallGoodsApplication.class, args);
    }

}
