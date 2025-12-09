package com.sgave.mall.sgavemalluser.service;


import com.sgave.mall.sgavemalluser.pojo.AdminUser;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
public interface AdminService {
    AdminUser login(String userName, String password);
}
