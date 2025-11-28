package com.sgave.mall.sgavemallshopping.dto;

import com.sgave.mall.sgavemallshopping.pojo.Goods;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/6/14
 */
@Data
public class GoodsDto extends Goods {
    private int collectStatus;

    // 添加构造方法
    public GoodsDto(Goods goods) {
        // 复制父类属性
        BeanUtils.copyProperties(goods, this);
    }
}
