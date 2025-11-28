package com.sgave.mall.sgavemallinventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sgave.mall.sgavemallinventory.pojo.Inventory;
import org.apache.ibatis.annotations.Param;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/10
 */
public interface InventoryMapper extends BaseMapper<Inventory> {

    int addStock(@Param("id") Long id, @Param("num") Integer num);
    int reduceStock(@Param("id") Integer id, @Param("num") Integer num);

}
