package com.sgave.mall.sgavemallshopping.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SgaveAddress {

    @TableId(type = IdType.AUTO)
    private Integer id;                 // 主键ID
    private String name;               // 收货人名称
    private Integer userId;            // 用户表的用户ID
    private String province;           // 行政区域表的省ID
    private String city;               // 行政区域表的市ID
    private String county;             // 行政区域表的区县ID
    private String addressDetail;      // 详细收货地址
    private String areaCode;           // 地区编码
    private String postalCode;         // 邮政编码
    private String tel;                // 手机号码
    private Boolean isDefault;         // 是否默认地址
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime addTime;     // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;  // 更新时间
    private Boolean deleted;           // 逻辑删除

}
