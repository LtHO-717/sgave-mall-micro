package com.sgave.mall.sgavemallgoods.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : yajun
 * @description :
 * @createDate : 2024/12/21
 */
@Configuration
public class MyBatisConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){

        // 初始化 MybatisPlusInterceptor 核心插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加自动分页插件 PaginationInnerInterceptor
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
