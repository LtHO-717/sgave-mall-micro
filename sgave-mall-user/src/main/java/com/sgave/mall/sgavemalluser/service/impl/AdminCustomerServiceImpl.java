package com.sgave.mall.sgavemalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemalluser.pojo.CustomerUser;
import com.sgave.mall.sgavemalluser.mapper.CustomerMapper;
import com.sgave.mall.sgavemalluser.service.AdminCustomerService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Service
public class AdminCustomerServiceImpl implements AdminCustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public IPage<CustomerUser> getUserList(String userName, Integer status, Integer current, Integer size) {
        Page<CustomerUser> page = new Page<>(current, size);
        LambdaQueryWrapper<CustomerUser> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(userName)){
            queryWrapper.like(CustomerUser::getUserName, userName);
        }
        if(status!=null){
            queryWrapper.eq(CustomerUser::getStatus, status);
        }

        return customerMapper.selectPage(page, queryWrapper);
    }
}
