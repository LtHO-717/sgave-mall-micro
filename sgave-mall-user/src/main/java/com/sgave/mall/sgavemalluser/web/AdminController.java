package com.sgave.mall.sgavemalluser.web;

import com.sgave.mall.sgavemalluser.pojo.AdminUser;
import com.sgave.mall.sgavemalluser.service.AdminService;
import com.sgave.mall.util.AdminJwtUtil;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@RestController
@Tag(name = "管理员")
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;


    @Operation(summary = "管理员登录")
    @PostMapping("/admin")
    public Object adminLogin(@RequestBody AdminUser adminUser) {
        if (adminUser.getUserName() == null || adminUser.getPassword() == null) {
            return ResponseUtil.badArgument();
        }
        AdminUser loginUser = adminService.login(adminUser.getUserName(), adminUser.getPassword());
        if (loginUser == null) {
            return ResponseUtil.fail(700, "管理员登录失败，用户名或密码错误");
        }

        String token = AdminJwtUtil.createToken(loginUser.getId(),loginUser.getUserName());
        return ResponseUtil.ok(Map.of(
                "token", token,
                "userInfo", loginUser
        ));
    }

    @Operation(summary = "管理员退出登录")
    @PostMapping("/logout")
    public Object adminLogout() {
        return ResponseUtil.ok("管理员退出登录成功");
    }
}
