package com.sgave.mall.sgavemallgateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgave.mall.sgavemallgateway.config.JwtProperties;
import com.sgave.mall.util.AdminJwtUtil;
import com.sgave.mall.util.ResponseUtil;
import com.sgave.mall.util.WebJwtUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String ADMIN_TOKEN_HEADER = "SGAVE-Mall-Admin-Token";
    private static final String USER_TOKEN_HEADER = "SGAVE-Mall-Token";

    // ⭐ 新增：前台接口路径前缀
    private static final String[] USER_PATH_PREFIX = {
            "/customer/**",
            "/cart/**",
            "/address/**",
            "/web/**",
            "/collect/**",
            "/order/**",
            "/footprint/**"
    };

    // ⭐ 新增：后台接口路径前缀
    private static final String[] ADMIN_PATH_PREFIX = {
            "/admin/**",
            "/goods/**",
            "/inventory/**",
            "/admin-order/**",
            "/user/**",
            "/excel/**"
    };

    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 检查当前请求是否在白名单中，如果是，则直接放行
        String path = exchange.getRequest().getURI().getPath();

        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }
        String token = getTokenFromRequest(exchange);

        boolean valid;
        if (isUserPath(path)) {
            // 前台 Web 接口 → 用 WebJwtUtil
            valid = (token != null && WebJwtUtil.verifyTokenAndGetUserId(token) != 0);
        } else {
            // 后台 Admin 接口 → 用 AdminJwtUtil
            valid = (token != null && AdminJwtUtil.verifyTokenAndGetUserId(token) != 0);
        }
        if (valid) {
            // 如果 JWT 校验通过，继续执行后续的请求处理
            return chain.filter(exchange);
        } else {
            return Mono.defer(() -> {
                // 调用 ResponseUtil 的 unlogin 方法
                Object response = ResponseUtil.fail(501, "请登录");  // 获取统一的未登录响应
                return buildErrorResponse(exchange, response);  // 返回响应
            });
        }
    }

    // 构建错误响应的处理方法
    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, Object response) {
        ServerHttpResponse httpResponse = exchange.getResponse();
        httpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 将 response 转换为 JSON 字符串
        DataBuffer buffer;
        try {
            buffer = exchange.getResponse().bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(response));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(e));  // 如果转换失败，抛出异常
        }

        return httpResponse.writeWith(Mono.just(buffer));  // 写入响应内容
    }

    /**
     * 检查当前请求是否在白名单中
     *
     * @param path
     * @return
     */
    private boolean isWhiteListed(String path) {
        // 简单的路径匹配，可以用 AntPathMatcher 支持更多的通配符匹配
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String pattern : jwtProperties.getWhiteList()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }


    // ⭐ 判断是否是前台 Web 接口
    private boolean isUserPath(String path) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : USER_PATH_PREFIX) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 从请求中获取 JWT 令牌
     *
     * @param exchange
     * @return
     */
    private String getTokenFromRequest(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        AntPathMatcher matcher = new AntPathMatcher();

        // 前台接口 → 使用用户 Token
        for (String pattern : USER_PATH_PREFIX) {
            if (matcher.match(pattern, path)) {
                return exchange.getRequest().getHeaders().getFirst(USER_TOKEN_HEADER);
            }
        }
        // 后台接口 → 使用管理员 Token
        for (String pattern : ADMIN_PATH_PREFIX) {
            if (matcher.match(pattern, path)) {
                return exchange.getRequest().getHeaders().getFirst(ADMIN_TOKEN_HEADER);
            }
        }
        // ⭐ 默认：当路径既不是前台也不是后台 → 默认使用后台 Token
        return exchange.getRequest().getHeaders().getFirst(ADMIN_TOKEN_HEADER);
    }

    @Override
    public int getOrder() {
        return -1; // 确保此过滤器在认证过程中执行
    }
}
