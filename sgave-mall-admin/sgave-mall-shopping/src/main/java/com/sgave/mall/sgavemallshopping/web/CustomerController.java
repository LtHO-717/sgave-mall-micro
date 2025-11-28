package com.sgave.mall.sgavemallshopping.web;

import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.service.CustomerService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import com.sgave.mall.util.ResponseUtil;
import com.sgave.mall.util.WebJwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/28
 */
@RestController
@RequestMapping("/customer")
@Tag(name = "用户")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Object register(@RequestBody CustomerUser customerUser) {
        CustomerUser registeredUser = customerService.register(customerUser);
        return ResponseUtil.ok(registeredUser);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Object login(@RequestBody CustomerUser customerUser, HttpSession session) {
        if (customerUser.getUserName() == null || customerUser.getPassword() == null) {
            return ResponseUtil.badArgument();
        }
        customerUser = customerService.login(customerUser.getUserName(), customerUser.getPassword());
        if (customerUser != null) {
            //session.setAttribute("customerUser", customerUser);
            String token = WebJwtUtil.createToken(customerUser.getId(), customerUser.getUserName());
            Map<String, Object> map = new HashMap<>();
            map.put("customer", customerUser);
            map.put("token", token);
            return ResponseUtil.ok(map);
        }
        return ResponseUtil.fail(700, "用户名或密码错误");
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Object logout() {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {

            customerService.logout(customerUser.getId());
            return ResponseUtil.ok();
        }
        return ResponseUtil.serious();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Object getCurrentUser() {
        // 获取当前登录用户的用户名
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser == null) {
            return ResponseUtil.fail(701, "用户不存在");
        }

        return ResponseUtil.ok(customerUser);
    }

}
