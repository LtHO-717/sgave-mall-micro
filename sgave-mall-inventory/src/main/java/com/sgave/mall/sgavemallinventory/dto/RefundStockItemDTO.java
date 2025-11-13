package com.sgave.mall.sgavemallinventory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefundStockItemDTO implements Serializable {
    private Long productId;
    private String goodsSn;
    private Integer quantity;
}
