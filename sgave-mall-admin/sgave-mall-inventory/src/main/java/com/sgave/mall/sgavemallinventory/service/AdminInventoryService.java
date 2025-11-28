package com.sgave.mall.sgavemallinventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallinventory.dto.CountDTO;
import com.sgave.mall.sgavemallinventory.pojo.Inventory;
import com.sgave.mall.sgavemallinventory.pojo.InventoryLock;
import com.sgave.mall.sgavemallinventory.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallinventory.pojo.InventoryLog;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
public interface AdminInventoryService {

    IPage<Inventory> getInventoryList(Integer page, Integer limit, String name, Byte minStatus, String sortField, String sortOrder);

    Inventory inventoryDetail(Integer id);

    Boolean lockBatch(List<InventoryLockDTO> inventoryLockDTOList);

    Boolean unlockBatch(List<InventoryLockDTO> inventoryLockDTOList);

    Boolean setMinStock(String goodsSn, Integer minStock);

    Boolean addInventory(Inventory inventory);

    Boolean replenishInventory(String goodsSn, Integer quantity);

    IPage<InventoryLog> getInventoryLogs(String goodsSn, String orderNo, Integer page, Integer limit, String sortField, String sortOrder);

    List<InventoryLock> getInventoryLocks(String goodsSn, String orderNo, Integer page, Integer limit, String sortField, String sortOrder);

    CountDTO count();
}
