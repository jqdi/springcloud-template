package com.company.datasource.mybatisplus.activerecord;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 审计字段模型基类
 * 
 * @author JQ棣
 */
public class AuditableModel<T extends Model<?>> extends Model<T> {

    // 创建时间：插入时填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 创建人：插入时填充
    @TableField(fill = FieldFill.INSERT)
    private Integer createBy;

    // 更新时间：插入和更新时都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 更新人：插入和更新时都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateBy;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

}
