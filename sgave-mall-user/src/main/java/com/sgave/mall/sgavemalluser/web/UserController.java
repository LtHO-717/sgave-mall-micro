package com.sgave.mall.sgavemalluser.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sgave.mall.sgavemalluser.dto.CustomerUser;
import com.sgave.mall.sgavemalluser.service.AdminCustomerService;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@RestController
@Tag(name = "用户管理")
@RequestMapping("/user")
public class UserController {

    @Resource
    private AdminCustomerService adminCustomerService;

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Object selectByPage(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        IPage<CustomerUser> userIPage = adminCustomerService.getUserList(username, status, page, limit);
        return ResponseUtil.okList(userIPage);
    }
}
