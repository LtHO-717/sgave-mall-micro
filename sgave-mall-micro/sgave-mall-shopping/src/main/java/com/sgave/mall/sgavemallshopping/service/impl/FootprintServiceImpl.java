package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemallshopping.mapper.FootprintMapper;
import com.sgave.mall.sgavemallshopping.mapper.GoodsMapper;
import com.sgave.mall.sgavemallshopping.pojo.Footprint;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.service.FootprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/27
 */
@Service
public class FootprintServiceImpl implements FootprintService {
    @Autowired
    private FootprintMapper footprintMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<Goods> getFootprints(Integer userId) {
        LambdaQueryWrapper<Footprint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Footprint::getUserId, userId);
        List<Footprint> footprints = footprintMapper.selectList(queryWrapper);
        if (footprints.isEmpty()) {
            return Collections.emptyList();
        }
        return goodsMapper.selectBatchIds(footprints.stream().map(Footprint::getGoodsId).toList());
    }

    @Override
    public void addFootprintGoods(Integer userId, Integer goodsId) {
        Footprint footprint = new Footprint();
        footprint.setUserId(userId);
        footprint.setGoodsId(goodsId);
        footprintMapper.insert(footprint);
    }

    @Override
    public void delFootprintGoods(Integer userId, Integer goodsId) {
        // 构建双条件查询包装器
        LambdaQueryWrapper<Footprint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Footprint::getUserId, userId)
                .eq(Footprint::getGoodsId, goodsId);
        footprintMapper.delete(queryWrapper);
    }
}
