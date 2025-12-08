package com.sgave.mall.sgavemallgoods.repository;

import com.sgave.mall.sgavemallgoods.dto.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/12/2
 */
public interface GoodsESRepository extends ElasticsearchRepository<Goods, Long> {
    // 无需写基础方法，继承后自动拥有 save/delete/findById 等方法
}
