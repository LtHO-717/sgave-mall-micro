package com.sgave.mall.sgavemallshopping.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : LtHO
 * @description : 商品搜索请求参数
 * @createDate : 2025/12/2
 */
@Data
public class GoodsSearchDTO {
    // 核心搜索关键词（匹配商品名称/详情）
    private String keyword;
    // 分类筛选（精准匹配）
    private String category;
    // 最低价格
    private BigDecimal minPrice;
    // 最高价格
    private BigDecimal maxPrice;
    // 排序字段（price、id 等）
    private String sortField = "id";
    // 排序类型（asc：升序，desc：降序）
    private String sortType = "desc";
    // 分页页码（默认第 1 页）
    private Integer pageNum = 1;
    // 每页条数（默认 10 条）
    private Integer pageSize = 10;
}
