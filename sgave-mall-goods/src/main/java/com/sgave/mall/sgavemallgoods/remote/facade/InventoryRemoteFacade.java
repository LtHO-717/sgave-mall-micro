package com.sgave.mall.sgavemallgoods.remote.facade;

import com.alibaba.fastjson.JSON;
import com.sgave.mall.sgavemallgoods.dto.Inventory;
import com.sgave.mall.sgavemallgoods.remote.InventoryRemoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class InventoryRemoteFacade {

    @Resource
    private InventoryRemoteService inventoryRemoteService;


    public Inventory selectOne(String goodSn) {
        try {
            Inventory response = inventoryRemoteService.selectOne(goodSn);
            log.info("查找库存,goodSn={}，返回：{}", goodSn, JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查找库存异常，goodSn={}", goodSn, e);
            return null;
        }
    }


}
