package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemallshopping.dto.GoodsDto;
import com.sgave.mall.sgavemallshopping.pojo.Collect;
import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.mapper.GoodsMapper;
import com.sgave.mall.sgavemallshopping.pojo.Inventory;
import com.sgave.mall.sgavemallshopping.remote.facade.InventoryRemoteFacade;
import com.sgave.mall.sgavemallshopping.service.CollectService;
import com.sgave.mall.sgavemallshopping.service.CustomerGoodsService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/27
 */
@Service
public class CustomerGoodsServiceImpl implements CustomerGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CollectService collectService;
    @Autowired
    private InventoryRemoteFacade inventoryRemoteFacade;

    @Override
    public IPage<Goods> getGoodsList(Integer goodsId, String goodsSn, String name, Integer current, Integer size) {
        Page<Goods> page = new Page<>(current, size);
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(goodsSn)) {
            queryWrapper.like(Goods::getGoodsSn, goodsSn);
        }
        if (!StringUtils.isBlank(name)) {
            queryWrapper.like(Goods::getName, name);
        }
        if (goodsId != null) {
            queryWrapper.eq(Goods::getId, goodsId);
        }
        queryWrapper.orderByDesc(Goods::getCreateTime);

        return goodsMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Goods getGoods(Integer goodsId) {
        //优先从缓存中查询
        String key = "goods:" + goodsId;
        Goods goods = (Goods) redisTemplate.opsForValue().get(key);
        if (goods == null) {
            goods = goodsMapper.selectById(goodsId);
            Inventory inventory = inventoryRemoteFacade.selectOne(goods.getGoodsSn());
            goods.setStock(inventory.getAvailableQuantity());
            if (goods != null) {
                redisTemplate.opsForValue().set(key, goods, 60, TimeUnit.MINUTES);
            }
        }
        return goods;
    }

    @Override
    public GoodsDto getGoodsDetail(Integer goodsId) {
        GoodsDto goodsDto = new GoodsDto(getGoods(goodsId));
        //获取当前登录用户，有就处理商品收藏状态
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            Collect collect = null;
            if (goodsDto != null) {
                collect = collectService.getCollectByUserIdAndGoodsId(goodsDto.getId(), customerUser.getId());
            }
            if (collect != null) {
                goodsDto.setCollectStatus(1);
            }
        }
        return goodsDto;
    }

}
