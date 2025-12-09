package com.sgave.mall.sgavemallorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.constant.AdminResponseCode;
import com.sgave.mall.sgavemallorder.constant.OrderConstant;
import com.sgave.mall.sgavemallorder.dto.*;
import com.sgave.mall.sgavemallorder.listener.RefundInventoryProducer;
import com.sgave.mall.sgavemallorder.mapper.OrderItemMapper;
import com.sgave.mall.sgavemallorder.mapper.OrderMapper;
import com.sgave.mall.sgavemallorder.pojo.Goods;
import com.sgave.mall.sgavemallorder.pojo.Order;
import com.sgave.mall.sgavemallorder.pojo.OrderItem;
import com.sgave.mall.sgavemallorder.remote.facade.GoodRemoteFacade;
import com.sgave.mall.sgavemallorder.service.AdminOrderService;
import com.sgave.mall.util.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private GoodRemoteFacade goodRemoteFacade;

    @Resource
    private RefundInventoryProducer refundInventoryProducer;

    private static final String REFUND_STOCK_TOPIC = "refund-stock-topic";


    @Override
    public IPage<Order> list(String orderSn, Short orderStatus, Integer page, Integer limit, String sort, String order) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 构造查询条件
        wrapper.eq(StringUtils.hasText(orderSn), Order::getOrderNo, orderSn)
                .eq(orderStatus != null, Order::getStatus, orderStatus);

        // 排序
        if (StringUtils.hasText(sort) && StringUtils.hasText(order)) {
            boolean isAsc = "asc".equalsIgnoreCase(order);
            wrapper.orderBy(true, isAsc, Order::getCreateTime);
        }

        // 分页
        Page<Order> pageInfo = new Page<>(page, limit);
        return orderMapper.selectPage(pageInfo, wrapper);
    }

    @Override
    public Object detail(Integer id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseUtil.ok(null);
        }
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderItems", orderItems);
        return ResponseUtil.ok(data);
    }


    @Override
    @Transactional
    public Object refund(OrderDTO orderDTO) {
        Long orderId = orderDTO.getOrderId();
        String refundMoney = orderDTO.getRefundMoney();
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return ResponseUtil.badArgument();
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        if (order.getPayAmount().compareTo(new BigDecimal(refundMoney)) != 0) {
            return ResponseUtil.badArgumentValue();
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getStatus().equals(OrderConstant.STATUS_REFUNDING)) {
            return ResponseUtil.fail(AdminResponseCode.ORDER_CONFIRM_NOT_ALLOWED, "订单不能确认收货");
        }

        Date now = new Date();
        // 设置订单取消状态
        order.setStatus(OrderConstant.STATUS_REFUNDED);
        order.setCancelTime(now);
        orderMapper.updateById(order);

        // 商品货品数量增加
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));


        List<InventoryLockDTO> inventoryLockDTOList = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            Goods goods = goodRemoteFacade.selectById(orderItem.getProductId());
            // 构建批量锁定 DTO
            InventoryLockDTO dto = new InventoryLockDTO();
            dto.setGoodsSn(goods.getGoodsSn());
            dto.setQuantity(orderItem.getQuantity());
            dto.setOrderNo(order.getOrderNo());
            inventoryLockDTOList.add(dto);
        }
        refundInventoryProducer.sendRefundStockMessage(inventoryLockDTOList);

        return ResponseUtil.ok();
    }

    /**
     * 订单发货
     *
     * @param orderId
     * @return
     */
    @Override
    public Object ship(String orderId) {
        if (orderId == null) {
            return ResponseUtil.badArgumentValue();
        }
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getId, orderId));
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getStatus().equals(OrderConstant.STATUS_PAY)) {
            return ResponseUtil.fail(OrderConstant.ORDER_CONFIRM_NOT_ALLOWED, "订单不能确认收货");
        }

        order.setStatus(OrderConstant.STATUS_SHIP);
        order.setDeliveryTime(new Date());
        orderMapper.updateById(order);
        return ResponseUtil.ok();
    }


    @Override
    public Object delete(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        // 如果订单不是关闭状态则不能删除
        Short status = order.getStatus();
        if (!status.equals(OrderConstant.STATUS_CANCEL) && !status.equals(OrderConstant.STATUS_REFUNDED) && !status.equals(OrderConstant.STATUS_FINISH)) {
            return ResponseUtil.fail(AdminResponseCode.ORDER_DELETE_FAILED, "订单不能删除");
        }
        // 删除订单
        orderMapper.deleteById(orderId);
        // 删除订单商品
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        return ResponseUtil.ok();
    }
}