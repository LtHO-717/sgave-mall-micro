package com.sgave.mall.sgavemallshopping.web;

import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.service.FootprintService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : LtHO
 * @description : 用户足迹
 * @createDate : 2025/5/29
 */
@RestController
@RequestMapping("/footprint")
@Tag(name = "用户足迹")
public class FootprintController {

    @Autowired
    private FootprintService footprintService;

    @Operation(summary = "足迹列表")
    @GetMapping("/list")
    public Object getFootprintList() {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            List<Goods> footprintList = footprintService.getFootprints(customerUser.getId());
            return ResponseUtil.ok(footprintList);
        }
        return ResponseUtil.unlogin();
    }

    @Operation(summary = "删除足迹")
    @DeleteMapping("/{goodsId}")
    public Object delCollect(@PathVariable(name = "goodsId") Integer goodsId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            footprintService.delFootprintGoods(customerUser.getId(),goodsId);
            return ResponseUtil.ok();
        }
        return ResponseUtil.unlogin();
    }

}
