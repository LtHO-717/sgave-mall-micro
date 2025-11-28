package com.sgave.mall.sgavemalluser.remote;

import com.sgave.mall.sgavemalluser.dto.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "sgave-mall-goods")
public interface GoodRemoteService {

    @PostMapping("/goods/selectBatchIds")
    List<Goods> selectBatchIds(@RequestBody List<Integer> goodIds);
}
