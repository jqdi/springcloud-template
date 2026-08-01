package com.company.common.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PageResp<T> {
	private Long total;// 总量
	private List<T> list;// 数据

	public static <T> PageResp<T> of(Long total, List<T> list) {
		return new PageResp<T>().setTotal(total).setList(list);
	}
}
