package com.company.web.controller;


import com.company.framework.util.PropertyUtils;
import com.company.order.api.feign.FeignTestFeign;
import com.company.order.api.request.RegisterOrderReq;
import com.company.order.api.response.OrderDetailResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feignTest")
@Slf4j
public class FeignTestController {

    @Autowired
    private FeignTestFeign feignTestFeign;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/getnoparam")
    public com.company.web.resp.OrderDetailResp getnoparam() {
		OrderDetailResp orderDetailResp = feignTestFeign.getnoparam();
		com.company.web.resp.OrderDetailResp resp = PropertyUtils.copyProperties(orderDetailResp, com.company.web.resp.OrderDetailResp.class);
		return resp;
    }

    @GetMapping(value = "/getnoparam33")
    public Optional<Integer> getnoparam33() {
        return Optional.ofNullable(1);
    }

    @GetMapping(value = "/getnoparam44")
    public Integer getnoparam44() {
        return 1;
    }

    @GetMapping(value = "/getnoparam55")
    public String getnoparam55() {
        return "11111111111111";
    }

    @GetMapping(value = "/getnoparam2")
    public com.company.web.resp.OrderDetailResp getnoparam2() {
        com.company.web.resp.OrderDetailResp resp = restTemplate.getForObject("http://template-order/feignTest/getnoparam", com.company.web.resp.OrderDetailResp.class);
        return resp;
    }

    @GetMapping(value = "/getnoparamList")
    public List<com.company.web.resp.OrderDetailResp>  getnoparamList() {
        List<OrderDetailResp> orderDetailRespList = feignTestFeign.getnoparamList();
        List<com.company.web.resp.OrderDetailResp> respList = PropertyUtils.copyArrayProperties(orderDetailRespList, com.company.web.resp.OrderDetailResp.class);
        return respList;
    }

    @GetMapping(value = "/getnoparamList2")
    public List<com.company.web.resp.OrderDetailResp> getnoparamList2() {
        ResponseEntity<List<com.company.web.resp.OrderDetailResp>> response = restTemplate.exchange(
                "http://template-order/feignTest/getnoparamList",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<com.company.web.resp.OrderDetailResp>>() {}
        );
        return response.getBody();

//        com.company.web.resp.OrderDetailResp[] result = restTemplate.getForObject("http://template-order/feignTest/getnoparamList", com.company.web.resp.OrderDetailResp[].class);
//        log.info("result:{}", JsonUtil.toJsonString(result));
//        return Arrays.asList(result);

//		OrderDetailResp orderDetailResp = feignTestFeign.getparam(orderCode);
//		com.company.web.resp.OrderDetailResp resp = PropertyUtils.copyProperties(orderDetailResp, com.company.web.resp.OrderDetailResp.class);
//		return resp;
    }

    @GetMapping(value = "/getparam")
    public com.company.web.resp.OrderDetailResp getparam(String orderCode) {
        com.company.web.resp.OrderDetailResp result = restTemplate.getForObject("http://template-order/feignTest/getparam?orderCode=" + orderCode, com.company.web.resp.OrderDetailResp.class);
        log.info("result:{}", result);
        return result;

//		OrderDetailResp orderDetailResp = feignTestFeign.getparam(orderCode);
//		com.company.web.resp.OrderDetailResp resp = PropertyUtils.copyProperties(orderDetailResp, com.company.web.resp.OrderDetailResp.class);
//		return resp;
    }

    @GetMapping(value = "/postbody")
    public com.company.web.resp.OrderDetailResp postbody(@RequestBody RegisterOrderReq registerOrderReq) {
        com.company.web.resp.OrderDetailResp result = restTemplate.postForObject("http://template-order/feignTest/postbody",registerOrderReq, com.company.web.resp.OrderDetailResp.class);
        log.info("result:{}", result);
        return result;

//        OrderDetailResp orderDetailResp = feignTestFeign.postbody(registerOrderReq);
//		com.company.web.resp.OrderDetailResp resp = PropertyUtils.copyProperties(orderDetailResp, com.company.web.resp.OrderDetailResp.class);
//		return resp;
    }
}
