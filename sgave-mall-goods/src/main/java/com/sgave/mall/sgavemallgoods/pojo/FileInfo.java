package com.sgave.mall.sgavemallgoods.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sgave.mall.dto.BaseBean;
import lombok.Data;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/11/7
 */
@Data
@TableName("sgave_files") // 映射数据库表
public class FileInfo extends BaseBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String fileKey;
    private String name;
    private String type;
    private Integer size;
    private String url;
}
