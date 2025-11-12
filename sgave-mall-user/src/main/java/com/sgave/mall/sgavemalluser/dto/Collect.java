package com.sgave.mall.sgavemalluser.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sgave.mall.dto.BaseBean;
import lombok.Data;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@Data
@TableName("collect")
public class Collect extends BaseBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer goodsId;
}
