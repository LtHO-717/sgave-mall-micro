package com.sgave.mall.sgavemallshopping.web;


import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.service.OrderService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@Tag(name = "用户订单服务")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;


    @Operation(summary = "根据状态查订单列表")
    @GetMapping("list")
    public Object list(@RequestParam(name = "status", required = false) Integer status) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.list(status, customerUser.getId());
        }
        return ResponseUtil.unlogin();
    }

    @Operation(summary = "订单详情", description = "根据订单ID获取订单详情")
    @GetMapping("/detail")
    public Object detail(@Parameter(name = "orderId", description = "订单ID") @RequestParam("orderId") Integer orderId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.detail(customerUser.getId(), orderId);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "商品结算")
    @GetMapping("checkout")
    public Object checkout(@RequestParam(name = "cartId",required = false) Integer cartId,
                           @RequestParam(name = "addressId",required = false) Integer addressId,
                           @RequestParam(name = "goodsId",required = false) Integer goodsId,
                           @RequestParam(name = "number",required = false) Integer number) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.checkout(cartId, addressId, customerUser.getId(), goodsId, number);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "提交订单")
    @PostMapping("submit")
    public Object submit(@RequestParam(name = "cartId",required = false) Integer cartId,
                         @RequestParam(name = "addressId") Integer addressId,
                         @RequestParam(name = "remark",required = false, defaultValue = "") String remark,
                         @RequestParam(name = "goodsId",required = false) Integer goodsId,
                         @RequestParam(name = "number",required = false) Integer number) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.submit(customerUser.getId(), cartId, addressId, remark, goodsId, number);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "模拟支付", description = "开发测试用，模拟支付")
    @PostMapping("/simulation-pay")
    public Object simulationPay(@RequestParam(name = "orderId") String orderId, HttpServletRequest request) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.simulationPay(customerUser.getId(), orderId, request);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "删除订单")
    @DeleteMapping("delete")
    public Object delete(@RequestParam(name = "orderId") Integer orderId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.delete(orderId, customerUser.getId());
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "申请退款")
    @GetMapping("refund")
    public Object refund(@RequestParam(name = "orderId") Integer orderId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.refund(orderId, customerUser.getId());
        }
        return ResponseUtil.unlogin();
    }

    @Operation(summary = "取消订单")
    @GetMapping("canal")
    public Object canal(@RequestParam(name = "orderId") Integer orderId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.canal(orderId, customerUser.getId());
        }
        return ResponseUtil.unlogin();
    }

    @Operation(summary = "确认收货")
    @PostMapping("confirm")
    public Object confirm(@RequestParam(name = "orderId") Integer orderId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            return orderService.confirm(orderId, customerUser.getId());
        }
        return ResponseUtil.unlogin();
    }



}