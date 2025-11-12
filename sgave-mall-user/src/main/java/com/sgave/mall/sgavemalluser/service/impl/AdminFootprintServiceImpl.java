package com.sgave.mall.sgavemalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemalluser.dto.Footprint;
import com.sgave.mall.sgavemalluser.dto.Goods;
import com.sgave.mall.sgavemalluser.mapper.FootprintMapper;
import com.sgave.mall.sgavemalluser.remote.facade.GoodRemoteFacade;
import com.sgave.mall.sgavemalluser.service.AdminFootprintService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Service
public class AdminFootprintServiceImpl implements AdminFootprintService {
    @Resource
    private FootprintMapper footprintMapper;
    @Resource
    private GoodRemoteFacade goodRemoteFacade;

    @Override
    public List<Goods> getFootprintByUserAndGoods(Integer userId, Integer goodsId) {
        LambdaQueryWrapper<Footprint> queryWrapper = new LambdaQueryWrapper<>();
        if(userId!=null){
            queryWrapper.eq(Footprint::getUserId, userId);
        }
        if(goodsId!=null){
            queryWrapper.eq(Footprint::getGoodsId, goodsId);
        }
        List<Footprint> footprints = footprintMapper.selectList(queryWrapper);
        return goodRemoteFacade.selectBatchIds(footprints.stream().map(Footprint::getGoodsId).toList());
    }
}
