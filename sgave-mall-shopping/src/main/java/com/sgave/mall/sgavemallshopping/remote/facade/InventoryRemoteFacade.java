package com.sgave.mall.sgavemallshopping.remote.facade;

import com.alibaba.fastjson.JSON;
import com.sgave.mall.sgavemallshopping.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallshopping.pojo.Inventory;
import com.sgave.mall.sgavemallshopping.remote.InventoryRemoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


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
            log.error("查找库存异常，goodIds={}", goodSn, e);
            return null;
        }
    }


    public Object lockBatch(List<InventoryLockDTO> inventoryLockDTOList) {
        try {
            Object response = inventoryRemoteService.lockBatch(inventoryLockDTOList);
            log.info("锁定库存,inventoryLockDTOList={}，返回：{}", JSON.toJSONString(inventoryLockDTOList), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("锁定库存异常，inventoryLockDTOList={}", JSON.toJSONString(inventoryLockDTOList), e);
            return null;
        }
    }


    public Object unlockBatch(List<InventoryLockDTO> inventoryLockDTOList) {
        try {
            Object response = inventoryRemoteService.unlockBatch(inventoryLockDTOList);
            log.info("解锁库存,inventoryLockDTOList={}，返回：{}", JSON.toJSONString(inventoryLockDTOList), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("解锁库存异常，inventoryLockDTOList={}", JSON.toJSONString(inventoryLockDTOList), e);
            return null;
        }
    }


    public Boolean reduceInventory(List<InventoryLockDTO> inventoryLockDTOList) {
        try {
            Boolean response = inventoryRemoteService.reduceInventory(inventoryLockDTOList);
            log.info("用户付款，减少库存,inventoryLockDTOList={}，返回：{}", JSON.toJSONString(inventoryLockDTOList), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("用户付款，减少库存异常，inventoryLockDTOList={}", JSON.toJSONString(inventoryLockDTOList), e);
            return null;
        }
    }


}
