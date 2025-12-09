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
@TableName("inventory")
public class Inventory extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String goodsSn;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer lockedQuantity;
    private Integer minStock;
    private Byte minStatus;
    private LocalDateTime lastCheckTime;
    private Integer version;
    private Integer isDeleted;
}
