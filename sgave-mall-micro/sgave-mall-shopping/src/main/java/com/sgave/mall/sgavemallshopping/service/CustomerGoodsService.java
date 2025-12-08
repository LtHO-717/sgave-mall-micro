package com.sgave.mall.sgavemallshopping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallshopping.pojo.Goods;

import java.util.List;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/21
 */
public interface CustomerGoodsService {
    IPage<Goods> getGoodsList(Integer goodsId, String goodsSn, String name, Integer current, Integer size);
    Goods getGoodsDetail(Integer goodsId);
    Goods getGoods(Integer goodsId);

    IPage<Goods> pageGoodsByIds(IPage<Goods> page, List<Integer> ids, String sortField, String sortType);
}
