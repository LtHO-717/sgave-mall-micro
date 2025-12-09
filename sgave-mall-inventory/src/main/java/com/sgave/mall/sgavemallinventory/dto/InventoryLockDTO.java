package com.sgave.mall.sgavemallinventory.dto;


import lombok.Data;


@Data
public class InventoryLockDTO {
    private String goodsSn;
    private Integer quantity;
    private String orderNo;
}
