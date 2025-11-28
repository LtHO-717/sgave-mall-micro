package com.sgave.mall.sgavemallshopping.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;


/**
 * @author : zeping
 * @description :
 * @createDate : 2025/6/16
 */
public interface OrderService {

    Object delete(Integer orderId, Integer userId);

    Object submit(Integer userId, Integer cartId, Integer addressId, String remark, Integer goodsId, Integer number);

    Object checkout(Integer cartId, Integer addressId, Integer userId, Integer goodsId, Integer number);

    Object refund(Integer orderId, Integer userId);

    Object list(Integer status, Integer userId);

    Object canal(Integer orderId, Integer userId);

    Object detail(Integer userId, Integer orderId);

    Object simulationPay(Integer userId, String orderId, HttpServletRequest request);

    Object confirm(Integer orderId, Integer userId);


}
