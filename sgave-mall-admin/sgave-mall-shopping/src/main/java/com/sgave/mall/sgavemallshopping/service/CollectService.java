package com.sgave.mall.sgavemallshopping.service;


import com.sgave.mall.sgavemallshopping.pojo.Collect;
import com.sgave.mall.sgavemallshopping.pojo.Goods;

import java.util.List;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/21
 */
public interface CollectService {
    List<Goods> getCollects(Integer userId);
    void addCollectGoods(Integer userId, Integer goodsId);
    void delCollectGoods(Integer userId,Integer goodsId);
    Collect getCollectByUserIdAndGoodsId(Integer goodsId, Integer userId);
}
