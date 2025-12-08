package com.sgave.mall.sgavemallshopping.web;

import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.service.CollectService;
import com.sgave.mall.sgavemallshopping.service.CustomerService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : LtHO
 * @description : 用户收藏
 * @createDate : 2025/5/29
 */
@RestController
@RequestMapping("/collect")
@Tag(name = "用户收藏")
public class CollectController {
    @Autowired
    private CollectService collectService;
    @Autowired
    private CustomerService customerService;

    @Operation(summary = "收藏商品")
    @PostMapping("/{goodsId}")
    public Object favoriteGoods(@PathVariable(name = "goodsId") Integer goodsId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            collectService.addCollectGoods(customerUser.getId(), goodsId);
            return ResponseUtil.ok();
        }
        return ResponseUtil.unlogin();
    }

    @Operation(summary = "收藏列表")
    @GetMapping("/list")
    public Object getCollectList() {
        // 获取当前登录用户的用户名
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            List<Goods> collectList = collectService.getCollects(customerUser.getId());
            return ResponseUtil.ok(collectList);
        }
        return ResponseUtil.unlogin();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{goodsId}")
    public Object delCollect(@PathVariable(name = "goodsId") Integer goodsId) {

        // 获取当前登录用户的用户名
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            collectService.delCollectGoods(customerUser.getId(),goodsId);
            return ResponseUtil.ok();
        }
        return ResponseUtil.unlogin();
    }
}
