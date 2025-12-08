package com.sgave.mall.sgavemallgoods.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Byte lockStatus;
    private Byte minStatus;
    private LocalDateTime lastCheckTime;
    private Integer isDeleted;
}
