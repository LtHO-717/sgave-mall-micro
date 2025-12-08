package com.sgave.mall.sgavemallshopping.service;


import com.sgave.mall.sgavemallshopping.dto.CartCheckDto;
import com.sgave.mall.sgavemallshopping.pojo.ShoppingCart;

import java.util.List;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/27
 */
public interface ShoppingCartService {
    // 商品加入购物车
    void addToCart(Integer userId, Integer goodsId, Integer number);
    // 获取购物车列表
    List<ShoppingCart> getShoppingCartList(Integer userId);
    // 更新购物车商品数量
    void updateCartQuantity(Integer cartId, Integer number);
    // 更新购物车选中状态
    void checkStatus(List<CartCheckDto> checks);
    // 删除购物车商品
    void deleteFromCart(List<Integer> cartIds);

    List<ShoppingCart> queryByUidAndChecked(Integer userId);
}
