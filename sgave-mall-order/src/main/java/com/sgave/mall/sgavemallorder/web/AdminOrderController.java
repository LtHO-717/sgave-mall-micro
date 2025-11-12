package com.sgave.mall.sgavemallorder.web;

import com.sgave.mall.sgavemallorder.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@RestController
@Tag(name = "订单管理")
@RequestMapping("/order")
public class AdminOrderController {

    @Resource
    private AdminOrderService adminOrderService;


    @Operation(summary = "查询订单列表接口")
    @GetMapping("/list")
    public Object list(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "orderSn", required = false) String orderSn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<Short> orderStatusArray,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "create_time") String sort,
            @RequestParam(defaultValue = "desc") String order) {
        return adminOrderService.list(userId, orderSn, start, end, orderStatusArray, page, limit, sort, order);
    }


    @Operation(summary = "查询订单详情接口")
    @GetMapping("/detail")
    public Object detail(@RequestParam("id") @NotNull Integer id) {
        return adminOrderService.detail(id);
    }


    @Operation(summary = "订单退款接口")
    @PostMapping("/refund")
    public Object refund(@RequestBody String body) {
        return adminOrderService.refund(body);
    }


    @Operation(summary = "发货接口")
    @PostMapping("/ship")
    public Object ship(@RequestParam("orderId") String orderId) {
        return adminOrderService.ship(orderId);
    }


    @Operation(summary = "删除订单接口")
    @PostMapping("/delete")
    public Object delete(@RequestBody String body) {
        return adminOrderService.delete(body);
    }
}
