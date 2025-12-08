package com.sgave.mall.sgavemallshopping.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgave.mall.sgavemallshopping.dto.GoodsSearchDTO;
import com.sgave.mall.sgavemallshopping.pojo.Goods;
import com.sgave.mall.sgavemallshopping.service.CustomerGoodsService;
import com.sgave.mall.sgavemallshopping.service.GoodsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/12/2
 */
@Slf4j
@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {
    @Autowired
    private CustomerGoodsService customerGoodsService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Override
    public IPage<Goods> searchGoods(GoodsSearchDTO searchDTO) {
        // 1. 构建 ES 搜索条件
        BoolQuery.Builder builder = buildSearchQuery(searchDTO);
        // 2. ES 排序，分页
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(
                        searchDTO.getSortType()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                searchDTO.getSortField()
        );
        PageRequest pageRequest = PageRequest.of(
                Math.toIntExact(searchDTO.getPageNum() - 1),//ES 分页从0开始
                Math.toIntExact(searchDTO.getPageSize()),
                sort
        );
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(builder.build()._toQuery())
                .withPageable(pageRequest)
                .build();
        // 3. 执行 ES 搜索，获取符合条件的商品列表
        SearchHits<Goods> searchHits = elasticsearchTemplate.search(query, Goods.class);
        // 4. 筛选 ID 字段
        List<Integer> goodsIds = searchHits.stream()
                .map(hit -> hit.getContent().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.info("ES 检索完成：关键词={}, 分类={}, 匹配商品 ID 数={}",
                searchDTO.getKeyword(), searchDTO.getCategory(), goodsIds.size());
// 5. 构建 MyBatis-Plus 分页对象
        IPage<Goods> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize(), searchHits.getTotalHits());
// 6. 查询 MySQL 商品数据
        return customerGoodsService.pageGoodsByIds(page, goodsIds, searchDTO.getSortField(), searchDTO.getSortType());
    }

    /**
     * 构建 ES 搜索条件
     */
    private BoolQuery.Builder buildSearchQuery(GoodsSearchDTO searchDTO) {
        // 构造模糊匹配条件（name 或 address 模糊匹配）
        BoolQuery.Builder boolQuery = QueryBuilders.bool();
        // 2.1 关键词搜索（should：名称/详情任意一个匹配，分词检索）
        if (StringUtils.isNotBlank(searchDTO.getKeyword())) {
            boolQuery.should(WildcardQuery.of(q -> q.field("name").value("*" + searchDTO.getKeyword() + "*"))._toQuery());
            boolQuery.should(WildcardQuery.of(q -> q.field("detail").value("*" + searchDTO.getKeyword() + "*"))._toQuery());

        }
        // 2.2 分类筛选（filter：精准匹配）
        if (StringUtils.isNotBlank(searchDTO.getCategory())) {
            boolQuery.filter(WildcardQuery.of(q -> q.field("category").value(searchDTO.getCategory()))._toQuery());
        }

        // 2.3 价格区间筛选（filter：范围查询）
        /*if (searchDTO.getMinPrice() != null) {
            boolQuery.filter(QueryBuilders);
        }
        if (searchDTO.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(searchDTO.getMaxPrice()));
        }*/
        //boolQuery.build();


       /* // 分类精准筛选
        if (searchDTO.getCategory() != null && !searchDTO.getCategory().trim().isEmpty()) {
            criteria = criteria.and(new Criteria("category").is(searchDTO.getCategory()));
        }

        // 价格区间筛选
        if (searchDTO.getMinPrice() != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(searchDTO.getMinPrice()));
        }
        if (searchDTO.getMaxPrice() != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(searchDTO.getMaxPrice()));
        }

        // 仅显示上架商品
        criteria = criteria.and(new Criteria("status").is(1));*/

        return boolQuery;
    }
}
