package com.sgave.mall.sgavemallshopping.service;


import com.sgave.mall.sgavemallshopping.pojo.Goods;

import java.util.List;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/21
 */
public interface FootprintService {
    List<Goods> getFootprints(Integer userId);
    void addFootprintGoods(Integer userId, Integer goodsId);
    void delFootprintGoods(Integer userId, Integer goodsId);
}
