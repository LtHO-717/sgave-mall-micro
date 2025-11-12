package com.sgave.mall.sgavemalluser.web;

import com.sgave.mall.sgavemalluser.dto.Goods;
import com.sgave.mall.sgavemalluser.service.AdminCollectService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@RestController
@RequestMapping("/collect")
@Tag(name = "用户收藏")
public class AdminCollectController {
    @Resource
    private AdminCollectService collectService;

    @GetMapping("/user")
    @Operation(summary = "获取用户足迹")
    public Object getFootList(@RequestParam Integer userId, @RequestParam Integer goodsId) {
        List<Goods> footGoods = collectService.getCollectsByUserAndGoods(userId,goodsId);
        return ResponseUtil.ok(footGoods);
    }
}
