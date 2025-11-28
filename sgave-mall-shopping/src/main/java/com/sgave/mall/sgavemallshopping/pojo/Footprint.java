package com.sgave.mall.sgavemallshopping.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : LtHO
 * @description : 用户足迹
 * @createDate : 2025/5/27
 */
@Data
@TableName("footprint")
public class Footprint extends BaseBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer goodsId;
}
