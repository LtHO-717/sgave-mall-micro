package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemallshopping.dto.GoodsDto;
import com.sgave.mall.sgavemallshopping.pojo.Collect;
import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.mapper.GoodsMapper;
import com.sgave.mall.sgavemallshopping.service.CollectService;
import com.sgave.mall.sgavemallshopping.service.CustomerGoodsService;
import com.sgave.mall.sgavemallshopping.service.CustomerService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    private CustomerService customerService;
    @Autowired
    private CollectService collectService;

    @Override
    public IPage<Goods> getGoodsList(Integer goodsId, String goodsSn, String name, Integer current, Integer size) {
        Page<Goods> page = new Page<>(current, size);
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(goodsSn)){
            queryWrapper.like(Goods::getGoodsSn, goodsSn);
        }
        if(!StringUtils.isBlank(name)){
            queryWrapper.like(Goods::getName, name);
        }
        if(goodsId!=null){
            queryWrapper.eq(Goods::getId, goodsId);
        }

        return goodsMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Goods getGoods(Integer goodsId) {
        //优先从缓存中查询
        String key = "goods:" + goodsId;
        Goods goods = (Goods) redisTemplate.opsForValue().get(key);
        if (goods == null) {
            goods = goodsMapper.selectById(goodsId);
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

    /**
     * 根据 ID 列表分页查询商品（保持 ES 排序）
     * @param page 分页对象
     * @param ids ES 检索出的商品 ID 列表
     * @param sortField 排序字段
     * @param sortType 排序类型
     * @return 分页结果
     */
    @Override
    public IPage<Goods> pageGoodsByIds(IPage<Goods> page, List<Integer> ids, String sortField, String sortType) {
        if (ids.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }
        // 构建查询条件：ID 在列表中 + 按 ES 排序（或自定义排序）
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<Goods>()
                .in(Goods::getId, ids)
                // 按指定字段排序（兼容 ES 排序）
                .orderBy(true, "asc".equalsIgnoreCase(sortType),
                        "price".equals(sortField) ? Goods::getPrice :
                                "id".equals(sortField) ? Goods::getId : Goods::getPrice);

        // 核心：通过 ID 列表的顺序强制排序（保证与 ES 检索顺序一致）
        /*if (!ids.isEmpty()) {
            StringBuilder orderBySql = new StringBuilder("FIELD(id, ");
            for (int i = 0; i < ids.size(); i++) {
                orderBySql.append(ids.get(i));
                if (i < ids.size() - 1) {
                    orderBySql.append(",");
                }
            }
            orderBySql.append(")");
            queryWrapper.last("ORDER BY " + orderBySql);
        }*/
        //用ES分页结果
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        page.setRecords(goods);
        return page;
    }
}
