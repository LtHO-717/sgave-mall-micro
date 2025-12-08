package com.sgave.mall.sgavemallshopping.vo;

import com.sgave.mall.sgavemallshopping.pojo.BaseBean;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : yajun
 * @description :
 * @createDate : 2025/1/4
 */
@Data
public class GoodsVO extends BaseBean {
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
    private Integer number;
}
