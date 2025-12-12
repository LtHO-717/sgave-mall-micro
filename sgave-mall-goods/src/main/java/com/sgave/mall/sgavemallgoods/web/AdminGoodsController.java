package com.sgave.mall.sgavemallgoods.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemallgoods.pojo.Goods;
import com.sgave.mall.sgavemallgoods.mapper.GoodsMapper;
import com.sgave.mall.sgavemallgoods.service.GoodsService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@RestController
@Tag(name = "商品管理")
@RequestMapping("/goods")
public class AdminGoodsController {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsService goodsService;

    @Operation(summary = "查询")
    @GetMapping("/query/{id}")
    public Object getGoods(@PathVariable("id") Integer id) {
        Goods goods = goodsMapper.selectById(id);
        return ResponseUtil.ok(goods);
    }

    @Operation(summary = "新增商品")
    @PostMapping("/add")
    public Object addGoods(@RequestBody Goods goods) {
        if (goods != null) {
            try {
                goodsService.saveGoods(goods);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.fail(402, e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("null");
        }
        System.out.println(goods);
        return ResponseUtil.ok(goods);
    }

    @Operation(summary = "分页查询")
    @GetMapping("queryPage")
    public Object selectByPage(
            @RequestParam(name = "goodsId", required = false) Integer goodsId,
            @RequestParam(name = "goodsSn", required = false) String goodsSn,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "2") Integer limit) {
        IPage<Goods> goodsIPage = goodsService.getGoodsList(goodsId, goodsSn, name, page, limit);
        System.out.println("数据总数:" + goodsIPage.getTotal());
        System.out.println("总页数:" + goodsIPage.getPages());
        System.out.println("当前页:" + goodsIPage.getCurrent());
        System.out.println("页大小:" + goodsIPage.getSize());
        return ResponseUtil.okList(goodsIPage);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    public Object updateGoods(@RequestBody Goods goods) {
        int result = goodsService.updateGoods(goods);
        if (result > 0) {
            return ResponseUtil.ok(goods);
        } else {
            return ResponseUtil.fail(505, "更新失败：" + goods);
        }
    }

    @Operation(summary = "删除")
    @DeleteMapping("/delete/{id}")
    public Object delGoods(@PathVariable("id") Integer id) {
        int result = goodsMapper.deleteById(id);
        if (result > 0) {
            return ResponseUtil.ok("删除成功：商品id:" + id);
        } else {
            return ResponseUtil.fail(500, "删除失败：商品id:" + id);
        }
    }

    @Operation(summary = "上架/下架商品")
    @PutMapping("/add/{id}")
    public Object updateGoodsShelf(
            @PathVariable("id") Integer id, @RequestParam(name = "status") Integer status) {
        int result = goodsService.updateGoodsShelf(id, status);
        if (result > 0) {
            if (status == 1) {
                return ResponseUtil.ok("上架成功,商品id:" + id);
            } else if (status == 0) {
                return ResponseUtil.ok("下架成功,商品id:" + id);
            } else {
                return ResponseUtil.ok("商品id:" + id + "  上/下架错误,传入status:" + status);
            }
        } else {
            return ResponseUtil.fail(505, "上架失败：商品id:" + id);
        }
    }

    @Operation(summary = "图片上传")
    @PostMapping("pic")
    public Object uploadPic(@RequestParam("file") MultipartFile file) {
        if (file != null) {
            return ResponseUtil.ok(goodsService.uploadPic(file));
        }
        return ResponseUtil.fail(402, "文件为null");
    }


    @Hidden
    @Operation(summary = "服务间调用接口，不对前端暴露")
    @PostMapping("/selectBatchIds")
    List<Goods> selectBatchIds(@RequestBody List<Integer> goodIds) {
        return goodsMapper.selectBatchIds(goodIds);
    }


    @Hidden
    @Operation(summary = "服务间调用接口，不对前端暴露")
    @GetMapping("/selectOne")
    Goods selectOne(@RequestParam("goodSn") String goodSn) {
        return goodsMapper.selectOne(new LambdaQueryWrapper<Goods>().eq(Goods::getGoodsSn, goodSn));
    }


    @Hidden
    @Operation(summary = "服务间调用接口，不对前端暴露")
    @GetMapping("/selectById")
    Goods selectById(@RequestParam("id") Integer id) {
        return goodsMapper.selectById(id);
    }
}
