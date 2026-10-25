package com.company.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("sc_city")
public class City extends AuditableModel<City> {
	private Long id;
	private Long provinceId;
	private String name;
	private Integer status;
	private Integer seq;
}
