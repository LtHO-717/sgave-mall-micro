package com.sgave.mall.sgavemallshopping.web;

import com.sgave.mall.sgavemallshopping.pojo.CustomerUser;
import com.sgave.mall.sgavemallshopping.pojo.SgaveAddress;
import com.sgave.mall.sgavemallshopping.service.AddressService;
import com.sgave.mall.sgavemallshopping.util.SecurityUtils;
import com.sgave.mall.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@Tag(name = "用户收货地址服务")
@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;


    @Operation(summary = "在个人中心查看收货地址列表")
    @GetMapping("list")
    public Object list() {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            List<SgaveAddress> addressList = addressService.list(customerUser.getId());
            return ResponseUtil.ok(addressList);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "添加收货地址")
    @PostMapping("save")
    public Object save(@RequestBody SgaveAddress address) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            Object error = validate(address);
            if (error != null) {
                return error;
            }
            return addressService.save(customerUser.getId(), address);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "修改收货地址")
    @PostMapping("update")
    public Object update(@RequestBody SgaveAddress address) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            Object error = validate(address);
            if (error != null) {
                return error;
            }
            return addressService.update(customerUser.getId(), address);
        }
        return ResponseUtil.unlogin();
    }


    @Operation(summary = "删除收货地址")
    @DeleteMapping("delete")
    public Object delete(@RequestParam Integer addressId) {
        CustomerUser customerUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(customerUser)) {
            SgaveAddress sgaveAddress = addressService.query(customerUser.getId(), addressId);
            if (Objects.isNull(sgaveAddress)) {
                return ResponseUtil.badArgumentValue();
            }
            addressService.delete(addressId);
            return ResponseUtil.ok("删除收货地址成功");
        }
        return ResponseUtil.unlogin();
    }


    private Object validate(SgaveAddress address) {
        String name = address.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgumentValue();
        }

        // 测试收货手机号码是否正确
        String mobile = address.getTel();
        if (StringUtils.isEmpty(mobile)) {
            return ResponseUtil.badArgumentValue();
        }

        String province = address.getProvince();
        if (StringUtils.isEmpty(province)) {
            return ResponseUtil.badArgumentValue();
        }

        String city = address.getCity();
        if (StringUtils.isEmpty(city)) {
            return ResponseUtil.badArgumentValue();
        }

        String county = address.getCounty();
        if (StringUtils.isEmpty(county)) {
            return ResponseUtil.badArgumentValue();
        }


        String areaCode = address.getAreaCode();
        if (StringUtils.isEmpty(areaCode)) {
            return ResponseUtil.badArgumentValue();
        }

        String detailedAddress = address.getAddressDetail();
        if (StringUtils.isEmpty(detailedAddress)) {
            return ResponseUtil.badArgumentValue();
        }

        Boolean isDefault = address.getIsDefault();
        if (isDefault == null) {
            return ResponseUtil.badArgumentValue();
        }
        return null;
    }

}