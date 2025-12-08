package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemallshopping.mapper.CustomerMapper;
import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/21
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CustomerUser register(CustomerUser customerUser) {
        //可以设计默认无效状态,等待后台审核后改为可登录状态
        customerMapper.insert(customerUser);
        return customerUser;
    }

    @Override
    public CustomerUser login(String userName, String password) {
        LambdaQueryWrapper<CustomerUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerUser::getUserName, userName)
                .eq(CustomerUser::getPassword, password);
        return customerMapper.selectOne(queryWrapper);
    }



    @Override
    public void logout(Integer customerId) {
        // 这里可以添加清除用户会话等逻辑,可以搜索Jwt黑名单机制
    }

    @Override
    public CustomerUser getCustomerByName(String customerName) {
        LambdaQueryWrapper<CustomerUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerUser::getUserName, customerName);
        return customerMapper.selectOne(queryWrapper);
    }
}
