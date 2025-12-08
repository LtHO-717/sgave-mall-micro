package com.sgave.mall.sgavemallshopping.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前请求中的 JWT Token
     */
    private static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("Sgave-Mall-Token");
        }
        return null;
    }

    /**
     * 获取当前登录用户的用户名
     */
    private static String getCurrentUsername() {
        String token = getToken();
        if (token != null) {
            try {
                // 解析 JWT Token 获取用户名
                DecodedJWT jwt = JWT.decode(token);
                return jwt.getSubject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 判断用户是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUsername() != null;
    }

    public static CustomerUser getCurrentUser() {
        if (isAuthenticated()) {
            // 获取当前登录用户的用户名
            String username = getCurrentUsername();
            if (username == null) {
                throw new IllegalArgumentException("无登录信息！");
            }
            // 查询用户信息
            CustomerService customerService = SpringUtil.getBean(CustomerService.class);
            return customerService.getCustomerByName(username);
        }
       return null;
    }
}