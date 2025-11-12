package com.sgave.mall.sgavemallinventory.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@Data
@TableName("inventory_logs")
public class InventoryLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 商品编号 */
    private String goodsSn;

    /** 商品名称 */
    private String goodsName;

    /** 变更类型：ADD/DEDUCT */
    private String changeType;

    /** 变更数量 */
    private Integer quantity;

    /** 订单编号 */
    private String orderNo;

    /** 操作人姓名 */
    private String operatorName;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
