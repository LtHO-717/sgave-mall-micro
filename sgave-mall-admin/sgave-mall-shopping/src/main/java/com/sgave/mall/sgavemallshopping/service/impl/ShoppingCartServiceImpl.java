package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sgave.mall.sgavemallshopping.dto.CartCheckDto;
import com.sgave.mall.sgavemallshopping.mapper.ShoppingCartMapper;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.pojo.ShoppingCart;
import com.sgave.mall.sgavemallshopping.service.CustomerGoodsService;
import com.sgave.mall.sgavemallshopping.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/27
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private CustomerGoodsService goodsService;

    @Override
    public void addToCart(Integer userId, Integer goodsId, Integer number) {
        // 检查购物车中是否已经存在该商品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .eq(ShoppingCart::getGoodsId, goodsId);
        ShoppingCart cartItem = shoppingCartMapper.selectOne(queryWrapper);

        if (cartItem != null) {
            // 如果存在，更新商品数量
            cartItem.setNumber(cartItem.getNumber() + number);
            shoppingCartMapper.updateById(cartItem);
        } else {
            // 如果不存在，添加新的购物车记录
            //根据商品Id查询商品信息
            Goods goodsItem = goodsService.getGoods(goodsId);
            ShoppingCart newCartItem = new ShoppingCart();
            newCartItem.setUserId(userId);
            newCartItem.setGoodsId(goodsId);
            newCartItem.setNumber(number);
            newCartItem.setGoodsSn(goodsItem.getGoodsSn());
            newCartItem.setPrice(goodsItem.getPrice());
            newCartItem.setPicUrl(goodsItem.getPicUrl());
            newCartItem.setGoodsName(goodsItem.getName());
            shoppingCartMapper.insert(newCartItem);
        }
    }

    @Override
    public List<ShoppingCart> getShoppingCartList(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        return shoppingCartMapper.selectList(queryWrapper);
    }

    @Override
    public void updateCartQuantity(Integer cartId, Integer number) {
        ShoppingCart cartItem = shoppingCartMapper.selectById(cartId);
        if (cartItem != null) {
            cartItem.setNumber(number);
            shoppingCartMapper.updateById(cartItem);
        }
    }

    @Override
    public void checkStatus(List<CartCheckDto> checks) {
        for (CartCheckDto check : checks) {
            ShoppingCart cartItem = shoppingCartMapper.selectById(check.getCartId());
            if (cartItem != null) {
                cartItem.setCheckStatus(check.getCheckStatus());
                shoppingCartMapper.updateById(cartItem);
            }
        }

    }

    @Override
    public void deleteFromCart(List<Integer> cartIds) {
        shoppingCartMapper.deleteByIds(cartIds);
    }

    @Override
    public List<ShoppingCart> queryByUidAndChecked(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId).eq(ShoppingCart::getCheckStatus, true);
        return shoppingCartMapper.selectList(queryWrapper);
    }
}
