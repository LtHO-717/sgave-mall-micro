package com.sgave.mall.sgavemallorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallorder.pojo.Order;
import com.sgave.mall.sgavemallorder.dto.OrderDTO;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
public interface AdminOrderService {
    Object ship(String orderId);

    IPage<Order> list(Integer userId, String orderSn, LocalDateTime start, LocalDateTime end, List<Short> orderStatusArray, Integer page, Integer limit, String sort, String order);

    Object detail(@NotNull Integer id);

    Object refund(OrderDTO orderDTO);

    Object delete(String orderId);
}
