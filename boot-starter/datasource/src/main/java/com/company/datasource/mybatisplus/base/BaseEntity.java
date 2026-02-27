package com.company.datasource.mybatisplus.base;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

public class BaseEntity {
    // 创建时间：插入时填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间：插入和更新时都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 创建人：插入时填充
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    // 更新人：插入和更新时都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
