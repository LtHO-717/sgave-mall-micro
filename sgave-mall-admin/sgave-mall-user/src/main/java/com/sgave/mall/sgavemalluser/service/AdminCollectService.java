package com.sgave.mall.sgavemalluser.service;


import com.sgave.mall.sgavemalluser.dto.Goods;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
public interface AdminCollectService {
    List<Goods> getCollectsByUserAndGoods(Integer userId, Integer goodsId);
}
