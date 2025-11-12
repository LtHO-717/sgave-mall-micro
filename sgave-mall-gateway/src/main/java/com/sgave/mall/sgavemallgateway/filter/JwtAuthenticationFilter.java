package com.sgave.mall.sgavemallgateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgave.mall.sgavemallgateway.config.JwtProperties;
import com.sgave.mall.util.JwtUtil;
import com.sgave.mall.util.ResponseUtil;
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

    private static final String AUTHORIZATION_HEADER = "SGAVE-Mall-Token";



    @Resource
    private JwtUtil jwtUtil;

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

        if (token != null && jwtUtil.verifyTokenAndGetUserId(token) != 0) {
            // 如果 JWT 校验通过，继续执行后续的请求处理
        } else {
            return Mono.defer(() -> {
                // 调用 ResponseUtil 的 unlogin 方法
                Object response = ResponseUtil.fail(501, "请登录");  // 获取统一的未登录响应
                return buildErrorResponse(exchange, response);  // 返回响应
            });
        }

        return chain.filter(exchange);
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


    /**
     * 从请求中获取 JWT 令牌
     * @param exchange
     * @return
     */
    private String getTokenFromRequest(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        return authorizationHeader;
    }

    @Override
    public int getOrder() {
        return -1; // 确保此过滤器在认证过程中执行
    }
}
