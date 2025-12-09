package com.sgave.mall.sgavemallgoods.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallgoods.pojo.Goods;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
public interface GoodsService {

    Goods saveGoods(Goods goods)throws IllegalArgumentException;

    IPage<Goods> getGoodsList(Integer goodsId, String goodsSn, String name,Integer current,Integer size);

    int updateGoods(Goods goods);

    int updateGoodsShelf(Integer goodsId,Integer status);

    Object uploadPic(MultipartFile file);
}
