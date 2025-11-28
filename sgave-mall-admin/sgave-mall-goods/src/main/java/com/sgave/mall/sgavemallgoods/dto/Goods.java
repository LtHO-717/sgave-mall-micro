package com.sgave.mall.sgavemallgoods.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sgave.mall.dto.BaseBean;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
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
