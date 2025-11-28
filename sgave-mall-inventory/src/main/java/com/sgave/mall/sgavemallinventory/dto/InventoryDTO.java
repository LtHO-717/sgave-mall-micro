package com.sgave.mall.sgavemallinventory.dto;


import com.sgave.mall.sgavemallinventory.pojo.Inventory;
import lombok.Data;

import java.util.List;

@Data
public class InventoryDTO {

    private List<Inventory> inventoryList;
    private Long totalGoodsCount;
    private Long warnGoodsCount;
    private Long lockGoodsCount;
    private Long canUseGoodsCount;

}
