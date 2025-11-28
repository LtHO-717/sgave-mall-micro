package com.sgave.mall.sgavemallshopping.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : LtHO
 * @description :
 * @createDate : 2025/5/21
 */
@Data
@TableName("customer")
public class CustomerUser extends BaseBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userName;
    private String fullName;
    private String password;
    private String phone;
    private int status;
}
