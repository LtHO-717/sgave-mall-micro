package com.sgave.mall.sgavemallshopping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallshopping.dto.GoodsSearchDTO;
import com.sgave.mall.sgavemallshopping.pojo.Goods;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/12/2
 */
public interface GoodsSearchService {

    IPage<Goods> searchGoods(GoodsSearchDTO searchDTO);
}
