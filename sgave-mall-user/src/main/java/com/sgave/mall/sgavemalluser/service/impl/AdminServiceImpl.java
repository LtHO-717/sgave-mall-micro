package com.sgave.mall.sgavemalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sgave.mall.sgavemalluser.pojo.AdminUser;
import com.sgave.mall.sgavemalluser.mapper.AdminMapper;
import com.sgave.mall.sgavemalluser.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Override
    public AdminUser login(String userName, String password) {
        LambdaUpdateWrapper<AdminUser> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AdminUser::getUserName, userName)
                .set(AdminUser::getPassword, password);
        AdminUser admin = adminMapper.selectOne(lambdaUpdateWrapper);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }
}
