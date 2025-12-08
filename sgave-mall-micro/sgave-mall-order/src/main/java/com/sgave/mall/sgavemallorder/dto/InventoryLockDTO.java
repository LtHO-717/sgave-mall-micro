package com.sgave.mall.sgavemallorder.dto;


import lombok.Data;


@Data
public class InventoryLockDTO {
    private String goodsSn;
    private Integer quantity;
    private String orderNo;

}
