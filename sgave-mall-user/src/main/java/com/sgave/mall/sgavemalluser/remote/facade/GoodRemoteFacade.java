package com.sgave.mall.sgavemalluser.remote.facade;

import com.alibaba.fastjson.JSON;
import com.sgave.mall.sgavemalluser.pojo.Goods;
import com.sgave.mall.sgavemalluser.remote.GoodRemoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class GoodRemoteFacade {

    @Resource
    private GoodRemoteService couponRemoteService;


    public List<Goods> selectBatchIds(List<Integer> goodIds) {
        try {
            List<Goods> response = couponRemoteService.selectBatchIds(goodIds);
            log.info("查找商品列表,goodIds={}，返回：{}", JSON.toJSONString(goodIds), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查找商品列表异常，goodIds={}", JSON.toJSONString(goodIds), e);
            return null;
        }
    }


}
