package com.sgave.mall.sgavemallorder.remote.facade;

import com.alibaba.fastjson.JSON;
import com.sgave.mall.sgavemallorder.dto.Goods;
import com.sgave.mall.sgavemallorder.remote.GoodRemoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class GoodRemoteFacade {

    @Resource
    private GoodRemoteService couponRemoteService;


    public Goods selectById(Integer goodId) {
        try {
            Goods response = couponRemoteService.selectById(goodId);
            log.info("查找商品,goodId={}，返回：{}", goodId, JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查找商品异常，goodIds={}", goodId, e);
            return null;
        }
    }


}
