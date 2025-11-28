package com.sgave.mall.sgavemallshopping.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : yajun
 * @description :
 * @createDate : 2025/1/4
 */
@Data
@TableName("sgave_goods") // 映射数据库表
public class Goods extends BaseBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String goodsSn;
    private String unit;
    private String detail;
    private String picUrl;
    private BigDecimal price;
    private String category;
    private Integer status;
    private Integer stock;
    private Integer version;
}
