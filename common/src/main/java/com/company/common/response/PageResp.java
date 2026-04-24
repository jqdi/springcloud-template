package com.company.common.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PageResp<T> {

    private Long total;
    private List<T> list;

    public static <T> PageResp<T> of(Long total, List<T> list) {
        return new PageResp<T>().setTotal(total).setList(list);
    }
}
