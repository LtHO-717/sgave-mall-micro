package com.sgave.mall.sgavemallshopping.remote;

import com.sgave.mall.sgavemallshopping.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallshopping.pojo.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "sgave-mall-inventory")
public interface InventoryRemoteService {

    @GetMapping("/inventory/selectOne")
    Inventory selectOne(@RequestParam(name = "goodsSn") String goodsSn);

    @PostMapping("/inventory/lockBatch")
    Object lockBatch(@RequestBody List<InventoryLockDTO> inventoryLockDTOList);

    @PostMapping("/inventory/unlockBatch")
    Object unlockBatch(@RequestBody List<InventoryLockDTO> inventoryLockDTOList);

}
