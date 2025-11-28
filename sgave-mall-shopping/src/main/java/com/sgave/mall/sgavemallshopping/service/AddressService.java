package com.sgave.mall.sgavemallshopping.service;


import com.sgave.mall.sgavemallshopping.pojo.SgaveAddress;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/6/13
 */
public interface AddressService {
    List<SgaveAddress> list(Integer userId);

    void delete(Integer addressId);

    Object save(Integer userId, SgaveAddress address);

    Object update(Integer userId, SgaveAddress address);

    SgaveAddress query(Integer userId, Integer addressId);
}
