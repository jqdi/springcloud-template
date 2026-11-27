package com.company.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user_client")
public class UserClient extends AuditableModel<UserClient> {
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("client_type")
    private String clientType;

    private String remark;
}
