package com.sgave.mall.sgavemalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemalluser.dto.Collect;
import com.sgave.mall.sgavemalluser.dto.Goods;
import com.sgave.mall.sgavemalluser.mapper.CollectMapper;
import com.sgave.mall.sgavemalluser.remote.facade.GoodRemoteFacade;
import com.sgave.mall.sgavemalluser.service.AdminCollectService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Service
public class AdminCollectServiceImpl implements AdminCollectService {
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private GoodRemoteFacade goodRemoteFacade;

    @Override
    public List<Goods> getCollectsByUserAndGoods(Integer userId, Integer goodsId) {
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        if(userId!=null){
            queryWrapper.eq(Collect::getUserId, userId);
        }
        if(goodsId!=null){
            queryWrapper.eq(Collect::getGoodsId, goodsId);
        }
        //queryWrapper.eq(Collect::getUserId, userId).eq(Collect::getGoodsId, goodsId);
        List<Collect> collects = collectMapper.selectList(queryWrapper);
        //根据用户足迹记录中的商品 ID，批量查询对应的商品信息。
        return goodRemoteFacade.selectBatchIds(collects.stream().map(Collect::getGoodsId).toList());
    }
}
