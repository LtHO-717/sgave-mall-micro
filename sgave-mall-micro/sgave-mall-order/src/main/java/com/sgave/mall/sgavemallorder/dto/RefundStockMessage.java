package com.sgave.mall.sgavemallorder.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class RefundStockMessage implements Serializable {
    private Integer orderId;
    private List<RefundStockItemDTO> items;
}
