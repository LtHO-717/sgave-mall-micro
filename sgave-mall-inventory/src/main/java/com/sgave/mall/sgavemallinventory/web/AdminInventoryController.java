package com.sgave.mall.sgavemallinventory.web;

import com.sgave.mall.sgavemallinventory.dto.Inventory;
import com.sgave.mall.sgavemallinventory.dto.InventoryLock;
import com.sgave.mall.sgavemallinventory.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallinventory.dto.InventoryLog;
import com.sgave.mall.sgavemallinventory.service.AdminInventoryService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@RestController
@RequestMapping("/inventory")
public class AdminInventoryController {

    @Resource
    private AdminInventoryService adminInventoryService;

    @Operation(summary = "分页查看库存列表")
    @GetMapping("/list")
    public Object inventoryList(@RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                @RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit,
                                @RequestParam(name = "create_time", required = false, defaultValue = "create_time") String sortField,
                                @RequestParam(name = "desc", required = false, defaultValue = "desc") String sortOrder) {
        List<Inventory> inventoryList = adminInventoryService.getInventoryList(page, limit, sortField, sortOrder);
        return ResponseUtil.ok(inventoryList);
    }


    @Operation(summary = "库存详情")
    @GetMapping("/detail")
    public Object inventoryDetail(@Parameter(name = "id", description = "库存id") @RequestParam(name = "id") Integer id) {
        Inventory inventory = adminInventoryService.inventoryDetail(id);
        return ResponseUtil.ok(inventory);
    }


    @Operation(summary = "批量锁定库存")
    @PostMapping("/lockBatch")
    public Object lockBatch(@RequestBody List<InventoryLockDTO> inventoryLockDTOList) {
        Boolean result = adminInventoryService.lockBatch(inventoryLockDTOList);
        if (result) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail(505, "批量锁定库存失败");
        }
    }


    @Operation(summary = "批量解锁库存")
    @PostMapping("/unlockBatch")
    public Object unlockBatch(@RequestBody List<InventoryLockDTO> inventoryLockDTOList) {
        Boolean result = adminInventoryService.unlockBatch(inventoryLockDTOList);
        if (result) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail(505, "批量解锁库存失败");
        }
    }


    @Operation(summary = "设置库存预警线")
    @PutMapping("/setMinStock")
    public Object setMinStock(@RequestParam("goodsSn") String goodsSn, @RequestParam("minStock") Integer minStock) {
        Boolean result = adminInventoryService.setMinStock(goodsSn, minStock);
        if (result) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail(505, "设置库存预警线失败");
        }
    }


    @PostMapping("/add")
    @Operation(summary = "新增库存")
    public Object addInventory(@RequestBody Inventory Inventory) {
        Boolean result = adminInventoryService.addInventory(Inventory);
        if (result) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail(505, "新增库存失败");
        }
    }


    @PutMapping("/replenish")
    @Operation(summary = "库存补货")
    public Object replenishInventory(@RequestParam("goodsSn") String goodsSn, @RequestParam("quantity") Integer quantity) {
        Boolean result = adminInventoryService.replenishInventory(goodsSn, quantity);
        if (result) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail(505, "库存补货失败");
        }
    }


    @Operation(summary = "分页查看日志")
    @GetMapping("/list")
    public Object getInventoryLogs(@RequestParam(name = "goodsSn", required = false) String goodsSn,
                                   @RequestParam(name = "orderNo", required = false) String orderNo,
                                   @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                   @RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit,
                                   @RequestParam(name = "create_time", required = false, defaultValue = "create_time") String sortField,
                                   @RequestParam(name = "desc", required = false, defaultValue = "desc") String sortOrder) {
        List<InventoryLog> inventoryLogList = adminInventoryService.getInventoryLogs(goodsSn, orderNo, page, limit, sortField, sortOrder);
        return ResponseUtil.ok(inventoryLogList);
    }


    @Operation(summary = "分页查看锁定信息")
    @GetMapping("/list")
    public Object getInventoryLocks(@RequestParam(name = "goodsSn", required = false) String goodsSn,
                                   @RequestParam(name = "orderNo", required = false) String orderNo,
                                   @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                   @RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit,
                                   @RequestParam(name = "create_time", required = false, defaultValue = "create_time") String sortField,
                                   @RequestParam(name = "desc", required = false, defaultValue = "desc") String sortOrder) {
        List<InventoryLock> inventoryLogList = adminInventoryService.getInventoryLocks(goodsSn, orderNo, page, limit, sortField, sortOrder);
        return ResponseUtil.ok(inventoryLogList);
    }

}
