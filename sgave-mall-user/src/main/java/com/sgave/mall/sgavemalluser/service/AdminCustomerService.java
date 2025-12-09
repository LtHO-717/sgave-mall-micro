package com.sgave.mall.sgavemalluser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemalluser.pojo.CustomerUser;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
public interface AdminCustomerService {
    IPage<CustomerUser> getUserList(String userName, Integer status, Integer current, Integer size);
}
