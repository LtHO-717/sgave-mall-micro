package com.sgave.mall.sgavemallshopping.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallshopping.dto.GoodsSearchDTO;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.service.CustomerGoodsService;
import com.sgave.mall.sgavemallshopping.service.FootprintService;
import com.sgave.mall.sgavemallshopping.service.GoodsSearchService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author : LtHO
 * @description : 商品
 * @createDate : 2025/5/29
 */
@RestController
@RequestMapping("/web")
@Tag(name = "商品")
public class GoodsController {
    @Autowired
    private CustomerGoodsService goodsService;
    @Autowired
    private GoodsSearchService goodsSearchService;

    @Operation(summary = "分页查询")
    @GetMapping("/goods")
    public Object selectByPage(
            @RequestParam(name = "goodsId", required = false) Integer goodsId,
            @RequestParam(name = "goodsSn",required = false)String goodsSn,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "limit",defaultValue = "10") Integer limit){
        IPage<Goods> goodsIPage = goodsService.getGoodsList(goodsId, goodsSn, name, page, limit);
        System.out.println("数据总数:" + goodsIPage.getTotal());
        System.out.println("总页数:" + goodsIPage.getPages());
        System.out.println("当前页:" + goodsIPage.getCurrent());
        System.out.println("页大小:" + goodsIPage.getSize());
        return ResponseUtil.okList(goodsIPage);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/goods/{goodsId}")
    public Object getGoodsDetail(@PathVariable(name = "goodsId") Integer goodsId) {
        Goods goods = goodsService.getGoodsDetail(goodsId);
        return ResponseUtil.ok(goods);
    }

    @Operation(summary = "商品ES搜索")
    @GetMapping("/es")
    public Object searchGoods(GoodsSearchDTO searchDTO) {
        IPage<Goods> result = goodsSearchService.searchGoods(searchDTO);
        return ResponseUtil.ok(result);
    }
}








