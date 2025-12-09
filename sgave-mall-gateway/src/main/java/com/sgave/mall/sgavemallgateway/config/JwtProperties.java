package com.sgave.mall.sgavemallgateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private List<String> whiteList;
    private List<String> webPathFilters;
    private List<String> adminPathFilters;

}
