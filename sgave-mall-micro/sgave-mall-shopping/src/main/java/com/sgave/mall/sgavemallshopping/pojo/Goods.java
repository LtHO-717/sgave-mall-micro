package com.sgave.mall.sgavemallshopping.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sgave.mall.dto.BaseBean;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

/**
 * @author : yajun
 * @description :
 * @createDate : 2025/1/4
 */
@Data
@TableName("sgave_goods") // 映射数据库表
// ES 索引映射（indexName 对应配置文件中的索引名，createIndex = true 自动创建索引）
@Document(indexName = "goods_index", createIndex = true)
public class Goods extends BaseBean {
    @TableId(type = IdType.AUTO)
    @Id // ES 文档主键（必须）
    @Field(type = FieldType.Integer, name = "id") // ES 字段类型：integer，字段名 id
    private Integer id;
    // 商品名称（ES 分词字段，指定分词器，如 ik_max_word 中文分词）
    @Field(type = FieldType.Text, name = "name", analyzer = "ik_max_word")
    private String name;
    @Field(type = FieldType.Keyword) // Keyword 类型，不分词，精准匹配
    private String goodsSn;
    // 核心：添加 @Transient，完全不同步到 ES
    @Transient
    private String unit;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String detail;
    @Transient
    private String picUrl;
    @Field(type = FieldType.Double) // 价格：支持范围查询/排序
    private BigDecimal price;
    @Field(type = FieldType.Keyword) // 分类：精准过滤/聚合
    private String category;
    @Transient
    private Integer status;
    @Transient
    private Integer stock;
    @Transient
    private Integer version;
}
