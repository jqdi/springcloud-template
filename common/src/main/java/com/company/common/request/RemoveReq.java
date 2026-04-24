package com.company.common.request;

import lombok.Data;

import java.util.List;

@Data
public class RemoveReq<T> {

    private List<T> idList;
}
