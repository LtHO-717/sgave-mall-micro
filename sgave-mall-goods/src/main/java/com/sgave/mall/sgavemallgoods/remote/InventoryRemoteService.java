package com.sgave.mall.sgavemallgoods.remote;

import com.sgave.mall.sgavemallgoods.dto.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "sgave-mall-inventory")
public interface InventoryRemoteService {

    @GetMapping("/inventory/selectOne")
    Inventory selectOne(@RequestParam(name = "goodsSn") String goodsSn);

}
