package com.sgave.mall.sgavemallshopping.service;


import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/21
 */
public interface CustomerService {
    CustomerUser register(CustomerUser customerUser);
    CustomerUser login(String username, String password);
    void logout(Integer customerId);
    CustomerUser getCustomerByName(String customerName);
}
