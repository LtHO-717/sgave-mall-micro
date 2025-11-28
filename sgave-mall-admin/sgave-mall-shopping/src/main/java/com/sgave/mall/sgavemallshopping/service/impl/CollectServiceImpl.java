package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemallshopping.pojo.Collect;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.mapper.CollectMapper;
import com.sgave.mall.sgavemallshopping.mapper.GoodsMapper;
import com.sgave.mall.sgavemallshopping.service.CollectService;
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
public class CollectServiceImpl implements CollectService {
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<Goods> getCollects(Integer userId) {
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getUserId, userId);
        List<Collect> collects = collectMapper.selectList(queryWrapper);
        if (collects.isEmpty()) {
            return Collections.emptyList();
        }
        return goodsMapper.selectBatchIds(collects.stream().map(Collect::getGoodsId).toList());

    }

    @Override
    public void addCollectGoods(Integer userId, Integer goodsId) {
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getUserId, userId);
        queryWrapper.eq(Collect::getGoodsId, goodsId);
        Collect collect = collectMapper.selectOne(queryWrapper);
        if (collect == null) {
            Collect collectNew = new Collect();
            collectNew.setUserId(userId);
            collectNew.setGoodsId(goodsId);
            collectMapper.insert(collectNew);
        }
    }

    @Override
    public void delCollectGoods(Integer userId, Integer goodsId) {
        // 构建双条件查询包装器
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getUserId, userId)
                .eq(Collect::getGoodsId, goodsId);
        collectMapper.delete(queryWrapper);
    }

    @Override
    public Collect getCollectByUserIdAndGoodsId(Integer goodsId, Integer userId) {
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getUserId, userId);
        queryWrapper.eq(Collect::getGoodsId, goodsId);
        return collectMapper.selectOne(queryWrapper);
    }
}
