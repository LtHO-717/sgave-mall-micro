package com.sgave.mall.sgavemallorder.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Data
@TableName("t_order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Integer productId;
    private String productName;
    private Long skuId;
    private String skuAttr;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

}