package com.sgave.mall.sgavemallinventory.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.sgave.mall.dto.BaseBean;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/6
 */
@Data
@TableName("inventory_locks")
public class InventoryLock extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 锁定ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 商品编号 */
    private String goodsSn;

    /** 订单编号 */
    private String orderNo;

    /** 锁定数量 */
    private Integer quantity;

    /** 锁定过期时间 */
    private LocalDateTime expireTime;

    /** 状态：1-锁定中，2-已解锁，3-已扣减 */
    private Integer status;

    /** 创建人ID */
    private Long createUserId;

}
