package com.sgave.mall.sgavemallinventory.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallinventory.dto.CountDTO;
import com.sgave.mall.sgavemallinventory.mapper.InventoryMapper;
import com.sgave.mall.sgavemallinventory.pojo.Inventory;
import com.sgave.mall.sgavemallinventory.dto.InventoryLockDTO;
import com.sgave.mall.sgavemallinventory.pojo.InventoryLog;
import com.sgave.mall.sgavemallinventory.service.AdminInventoryService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@Tag(name = "库存管理")
@RestController
@RequestMapping("/inventory")
public class AdminInventoryController {

    @Resource
    private AdminInventoryService adminInventoryService;

    @Resource
    private InventoryMapper inventoryMapper;

    @Operation(summary = "分页查看库存列表")
    @GetMapping("/list")
    public Object inventoryList(@RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                @RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit,
                                @RequestParam(name = "name", required = false) String name,
                                @RequestParam(name = "goodsSn", required = false) String goodsSn,
                                @RequestParam(name = "minStatus", required = false) Byte minStatus,
                                @RequestParam(name = "create_time", required = false, defaultValue = "create_time") String sortField,
                                @RequestParam(name = "desc", required = false, defaultValue = "desc") String sortOrder) {
        IPage<Inventory> inventoryIPage = adminInventoryService.getInventoryList(page, limit, name, goodsSn, minStatus, sortField, sortOrder);
        return ResponseUtil.okList(inventoryIPage);
    }


    @Operation(summary = "统计库存数据")
    @GetMapping("/count")
    public Object count() {
        CountDTO countDTO = adminInventoryService.count();
        return ResponseUtil.ok(countDTO);
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
    @PutMapping("/minStock")
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
            return ResponseUtil.fail(505, "库存已经存在");
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
    @GetMapping("/log/list")
    public Object getInventoryLogs(@RequestParam(name = "goodsSn", required = false) String goodsSn,
                                   @RequestParam(name = "orderNo", required = false) String orderNo,
                                   @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                   @RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit,
                                   @RequestParam(name = "create_time", required = false, defaultValue = "create_time") String sortField,
                                   @RequestParam(name = "desc", required = false, defaultValue = "desc") String sortOrder) {
        IPage<InventoryLog> inventoryLogIPage = adminInventoryService.getInventoryLogs(goodsSn, orderNo, page, limit, sortField, sortOrder);
        return ResponseUtil.okList(inventoryLogIPage);
    }


    @Hidden
    @Operation(summary = "服务间调用接口，不对前端暴露")
    @GetMapping("/selectOne")
    public Inventory selectOne(@RequestParam(name = "goodsSn") String goodsSn) {
        return inventoryMapper.selectOne(new QueryWrapper<Inventory>().eq("goods_sn", goodsSn));
    }


    @Hidden
    @Operation(summary = "用户付款，减少库存。服务间调用接口，不对前端暴露")
    @PostMapping("/reduce")
    public Boolean reduceInventory(@RequestBody List<InventoryLockDTO> inventoryLockDTOList) {
        return adminInventoryService.reduceInventory(inventoryLockDTOList);
    }
}
