package com.company.openapi.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.company.openapi.resp.CreateOrderResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.company.framework.sequence.SequenceGenerator;
import com.company.openapi.req.CreateOrderReq;
import com.google.common.collect.Maps;

/**
 * demo
 * 
 * @author JQ棣
 *
 */
@Validated
@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private SequenceGenerator sequenceGenerator;

	/**
	 * 测试无参
	 */
	@GetMapping("/info")
	public Map<String, String> info() {
		Map<String, String> result = Maps.newHashMap();
		result.put("name", "获取本appid对应的信息");
        return result;
	}

	/**
	 * 测试url参数
	 */
	@GetMapping("/get")
	public Map<String, String> get(@NotEmpty(message = "订单号不能为空") String orderCode) {
		Map<String, String> result = Maps.newHashMap();
		result.put("orderCode", orderCode);
		result.put("productCode", "2222222");
		result.put("orderid", "" + sequenceGenerator.nextId());
        return result;
    }

    /**
     * 测试url参数
     */
    @GetMapping("/get-entity")
    public CreateOrderResp get2(@NotEmpty(message = "订单号不能为空") String orderCode) {
        CreateOrderResp resp = new CreateOrderResp();
        resp.setOrderCode(orderCode);
        resp.setProductCode("2222222");
        resp.setOrderid("" + sequenceGenerator.nextId());
        return resp;
    }

	/**
	 * 测试body参数
	 */
	@PostMapping("/create")
	public Map<String, String> create(@Valid @RequestBody CreateOrderReq createOrderReq) {
		Map<String, String> result = Maps.newHashMap();
		result.put("orderCode", createOrderReq.getOrderCode());
		result.put("productCode", createOrderReq.getProductCode());
		result.put("orderid", "" + sequenceGenerator.nextId());
        return result;
	}
	
	/**
	 * 测试form参数
	 */
	@PostMapping("/create2")
	public Map<String, String> create2(CreateOrderReq createOrderReq) {
		Map<String, String> result = Maps.newHashMap();
		result.put("orderCode", createOrderReq.getOrderCode());
		result.put("productCode", createOrderReq.getProductCode());
		result.put("orderid", "" + sequenceGenerator.nextId());
		return result;
	}

}
