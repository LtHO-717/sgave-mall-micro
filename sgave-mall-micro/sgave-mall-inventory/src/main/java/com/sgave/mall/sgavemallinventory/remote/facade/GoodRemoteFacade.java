package com.sgave.mall.sgavemallinventory.remote.facade;

import com.alibaba.fastjson.JSON;
import com.sgave.mall.sgavemallinventory.pojo.Goods;
import com.sgave.mall.sgavemallinventory.remote.GoodRemoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class GoodRemoteFacade {

    @Resource
    private GoodRemoteService couponRemoteService;


    public Goods selectOne(String goodSn) {
        try {
            Goods response = couponRemoteService.selectOne(goodSn);
            log.info("查找商品,goodIds={}，返回：{}", goodSn, JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查找商品异常，goodIds={}", goodSn, e);
            return null;
        }
    }


}
