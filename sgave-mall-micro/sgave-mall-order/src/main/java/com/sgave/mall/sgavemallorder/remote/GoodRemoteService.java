package com.sgave.mall.sgavemallorder.remote;

import com.sgave.mall.sgavemallorder.dto.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(value = "sgave-mall-goods")
public interface GoodRemoteService {

    @GetMapping("/admin/goods/selectById")
    Goods selectById(@RequestParam("id") Integer id);
}
