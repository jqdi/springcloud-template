package com.company.openapi.resp;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderResp {
	String orderCode;
	String productCode;
	String orderid;
}
