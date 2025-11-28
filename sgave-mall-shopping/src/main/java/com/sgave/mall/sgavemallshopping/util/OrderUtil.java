package com.sgave.mall.sgavemallshopping.util;




import com.sgave.mall.sgavemallshopping.pojo.Order;

import java.util.ArrayList;
import java.util.List;

/*
 * 订单流程：下单成功 → 支付订单 → 发货 → 收货（完成）
 *
 * 订单状态：
 * 0 待支付：订单已创建，用户尚未支付；
 * 1 已支付：用户已支付，商家尚未发货；
 * 2 已发货：商家已发货，用户未确认收货；
 * 3 已完成：用户已确认收货，订单结束；
 * 4 已取消：用户主动取消或系统超时取消；
 * 5 退款中：用户申请退款，退款流程处理中。
 *
 * 各状态下用户可执行的操作：
 * - 待支付（0）：可“取消订单”或“立即支付”；
 * - 已支付（1）：可“申请退款”；
 * - 已发货（2）：可“确认收货”；
 * - 已完成（3）：可“申请售后”、“删除订单”、“评价订单”或“再次购买”；
 * - 已取消（4）：可“删除订单”；
 * - 退款中（5）：不可执行其他操作。
 */

public class OrderUtil {

    public static String orderStatusText(Order order) {
        Short status = order.getStatus(); // 改为新的status字段

        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            case 5 -> "退款中";
            default -> throw new IllegalStateException("不支持的订单状态: " + status);
        };
    }

    public static OrderHandleOption build(Order order) {
        Short status = order.getStatus();
        OrderHandleOption handleOption = new OrderHandleOption();

        switch (status) {
            case 0 -> { // 待支付
                handleOption.setCancel(true);
                handleOption.setPay(true);
            }
            case 1 -> handleOption.setRefund(true); // 已支付
            case 2 -> handleOption.setConfirm(true); // 已发货
            case 3 -> { // 已完成
                handleOption.setDelete(true);
                handleOption.setComment(true);
                handleOption.setRebuy(true);
                handleOption.setAftersale(true);
            }
            case 4 -> handleOption.setDelete(true); // 已取消
            case 5 -> { /* 退款中，无操作 */ }
            default -> throw new IllegalStateException("不支持的订单状态: " + status);
        }

        return handleOption;
    }

    public static List<Byte> orderStatus(Integer showType) {
        if (showType == null || showType == 0) {
            return null; // 全部订单
        }

        List<Byte> statusList = new ArrayList<>();

        switch (showType) {
            case 1 -> statusList.add((byte) 0); // 待支付
            case 2 -> statusList.add((byte) 1); // 待发货（已支付）
            case 3 -> statusList.add((byte) 2); // 待收货（已发货）
            case 4 -> statusList.add((byte) 3); // 已完成（可评价）
            default -> {
                return null;
            }
        }

        return statusList;
    }

    public static boolean isUnpaid(Order order) {
        return order.getStatus() == 0;
    }

    public static boolean isPaid(Order order) {
        return order.getStatus() == 1;
    }

    public static boolean isShipped(Order order) {
        return order.getStatus() == 2;
    }

    public static boolean isCompleted(Order order) {
        return order.getStatus() == 3;
    }

    public static boolean isCanceled(Order order) {
        return order.getStatus() == 4;
    }

    public static boolean isRefunding(Order order) {
        return order.getStatus() == 5;
    }

    public static boolean hasPayed(Order order) {
        return order.getStatus() != 0 && order.getStatus() != 4;
    }
}

