package com.sgave.mall.sgavemallshopping.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : yajun
 * @description :
 * @createDate : 2025/1/4
 */
@Data
public abstract class BaseBean implements Serializable {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime = new Date();
}
