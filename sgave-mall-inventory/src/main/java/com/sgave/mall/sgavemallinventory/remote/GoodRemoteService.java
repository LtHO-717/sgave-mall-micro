package com.sgave.mall.sgavemallinventory.remote;

import com.sgave.mall.sgavemallinventory.pojo.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "sgave-mall-goods")
public interface GoodRemoteService {

    @GetMapping("/goods/selectOne")
    Goods selectOne(@RequestParam("goodSn") String goodSn);

}
