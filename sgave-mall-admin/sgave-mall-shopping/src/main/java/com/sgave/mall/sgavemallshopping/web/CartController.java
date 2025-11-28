package com.sgave.mall.sgavemallshopping.web;

import com.sgave.mall.sgavemallshopping.dto.CartCheckDto;
import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.pojo.ShoppingCart;
import com.sgave.mall.sgavemallshopping.service.CustomerService;
import com.sgave.mall.sgavemallshopping.service.ShoppingCartService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : LtHO
 * @description : 用户购物车
 * @createDate : 2025/5/29
 */
@RestController
@RequestMapping("/cart")
@Tag(name = "用户购物车")
public class CartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CustomerService customerService;

    /**
     * 商品加入购物车
     * @param cart
     * @return 响应信息
     */
    @Operation(summary = "商品加入购物车")
    @PostMapping("/add")
    public Object addToCart(@RequestBody ShoppingCart cart) {
        //CustomerUser customerUser = (CustomerUser) session.getAttribute("customerUser");
        //获取当前登录用户，有就处理商品收藏状态
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            // 查询用户信息
            //CustomerUser customerUser = customerService.getCustomerByName(username);
            Integer number = cart.getNumber();
            Integer goodsId = cart.getGoodsId();
            shoppingCartService.addToCart(customerUser.getId(), goodsId, number);
            return ResponseUtil.ok();
        }
        return ResponseUtil.unlogin();
    }

    /**
     * 获取购物车列表
     * @param
     * @return 购物车列表
     */
    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public Object getShoppingCartList() {
        //获取当前登录用户，有就处理商品收藏状态
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (customerUser != null) {
            List<ShoppingCart> shoppingCarts = shoppingCartService.getShoppingCartList(customerUser.getId());
            return ResponseUtil.ok(shoppingCarts);
        }
        return ResponseUtil.unlogin();
    }

    /**
     * 更新购物车商品数量
     * @param cartId 购物车记录ID
     * @param number 商品数量
     * @return 响应信息
     */
    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/updateNumbuer")
    public Object updateCartQuantity(@RequestParam Integer cartId, @RequestParam Integer number) {
        shoppingCartService.updateCartQuantity(cartId, number);
        return ResponseUtil.ok("购物车商品数量已更新");
    }

    /**
     * 更新购物车选中状态
     * @param cartId 购物车记录ID
     * @param checkStatus 选中状态
     * @return 响应信息
     */
    @Operation(summary = "更新购物车选中状态")
    @PutMapping("/checkStatus")
    public Object checkStatus(@RequestBody List<CartCheckDto> checks) {
        if(checks!=null && !checks.isEmpty()){
            shoppingCartService.checkStatus(checks);
            return ResponseUtil.ok("购物车商品选中状态已更新");
        }
        return ResponseUtil.badArgument();
    }

    /**
     * 删除购物车商品
     * @param cartIds 购物车记录ID,可批量
     * @return 响应信息
     */
    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/batch")
    public Object deleteFromCart(@RequestBody List<Integer> cartIds) {
        // 检查参数是否为空
        if (cartIds == null || cartIds.isEmpty()) {
            return ResponseUtil.badArgument();
        }
        shoppingCartService.deleteFromCart(cartIds);
        return ResponseUtil.ok("所选购物车商品记录已删除");
    }
}
