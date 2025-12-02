package com.sgave.mall.sgavemallorder.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallorder.pojo.Order;
import com.sgave.mall.sgavemallorder.dto.OrderDTO;
import com.sgave.mall.sgavemallorder.service.AdminOrderService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
@RequestMapping("/admin-order")
public class AdminOrderController {

    @Resource
    private AdminOrderService adminOrderService;


    @Operation(summary = "查询订单列表接口")
    @GetMapping("/list")
    public Object list(
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "orderSn", required = false) String orderSn,
            @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "orderStatusArray", required = false) List<Short> orderStatusArray,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "sort", defaultValue = "create_time") String sort,
            @RequestParam(name = "order", defaultValue = "desc") String order) {
        IPage<Order> orderIPage = adminOrderService.list(userId, orderSn, start, end, orderStatusArray, page, limit, sort, order);
        return ResponseUtil.okList(orderIPage);
    }


    @Operation(summary = "查询订单详情接口")
    @GetMapping("/detail")
    public Object detail(@RequestParam("orderId") Integer orderId) {
        return adminOrderService.detail(orderId);
    }


    @Operation(summary = "订单退款接口")
    @PostMapping("/refund")
    public Object refund(@RequestBody OrderDTO orderDTO) {
        return adminOrderService.refund(orderDTO);
    }


    @Operation(summary = "发货接口")
    @PostMapping("/ship")
    public Object ship(@RequestParam("orderId") String orderId) {
        return adminOrderService.ship(orderId);
    }


    @Operation(summary = "删除订单接口")
    @DeleteMapping("/delete")
    public Object delete(@RequestParam("orderId") String orderId) {
        return adminOrderService.delete(orderId);
    }
}
