package com.sgave.mall.sgavemallshopping.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/27
 */
@Data
@TableName("shopping_cart")
public class ShoppingCart extends BaseBean{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private String goodsName;
    private String goodsSn;
    private String picUrl;
    private BigDecimal price;
    private Integer number;
    private Integer checkStatus;
}
