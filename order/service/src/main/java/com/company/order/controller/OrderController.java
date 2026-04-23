package com.company.order.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.company.framework.globalresponse.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.company.framework.context.HeaderContextUtil;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.PropertyUtils;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.enums.PayRefundApplyEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.request.OrderCancelReq;
import com.company.order.api.request.OrderFinishReq;
import com.company.order.api.request.OrderPaySuccessReq;
import com.company.order.api.request.OrderReceiveReq;
import com.company.order.api.request.OrderRefundApplyReq;
import com.company.order.api.request.OrderRefundFailReq;
import com.company.order.api.request.OrderRefundFinishReq;
import com.company.order.api.request.OrderReq;
import com.company.order.api.request.RegisterOrderReq;
import com.company.order.api.response.Order4Resp;
import com.company.order.api.response.OrderDetailResp;
import com.company.order.api.response.OrderDetailResp.TextValueResp;
import com.company.order.api.response.OrderRefundApplyResp;
import com.company.order.api.response.OrderResp;
import com.company.order.entity.Order;
import com.company.order.entity.OrderProduct;
import com.company.order.entity.PayRefundApply;
import com.company.order.mapper.PayRefundApplyMapper;
import com.company.order.service.OrderProductService;
import com.company.order.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController implements OrderFeign {

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderProductService orderProductService;
	@Autowired
	private PayRefundApplyMapper payRefundApplyMapper;

	@Override
	public Void registerOrder(@RequestBody RegisterOrderReq registerOrderReq) {
		Integer userId = registerOrderReq.getUserId();
		String orderCode = registerOrderReq.getOrderCode();
		String orderType = registerOrderReq.getOrderType();
		OrderEnum.SubStatusEnum subStatusEnum = registerOrderReq.getSubStatusEnum();
		BigDecimal productAmount = registerOrderReq.getProductAmount();
		BigDecimal orderAmount = registerOrderReq.getOrderAmount();
		BigDecimal reduceAmount = registerOrderReq.getReduceAmount();
		if (reduceAmount == null) {
			reduceAmount = BigDecimal.ZERO;
		}
		BigDecimal needPayAmount = registerOrderReq.getNeedPayAmount();
		String subOrderUrl = registerOrderReq.getSubOrderUrl();
		String attach = registerOrderReq.getAttach();

		Order order = orderService.saveOrUpdate(userId, orderType, orderCode, subStatusEnum, productAmount,
				orderAmount, reduceAmount, needPayAmount, subOrderUrl, attach);

		List<OrderProduct> orderProductList = orderProductService.selectByOrderCode(orderCode);
		if (orderProductList.isEmpty()) {
			List<RegisterOrderReq.OrderProductReq> orderProductReqList = registerOrderReq.getProductList();
			if (!CollectionUtils.isEmpty(orderProductReqList)) {
				List<OrderProduct> orderProductList4Insert = orderProductReqList.stream().map(v -> {
					OrderProduct orderProduct = new OrderProduct();
					orderProduct.setOrderCode(orderCode);
					orderProduct.setNumber(v.getNumber());
					orderProduct.setOriginAmount(v.getOriginAmount());
					orderProduct.setSalesAmount(v.getSalesAmount());
					orderProduct.setAmount(v.getSalesAmount().multiply(new BigDecimal(v.getNumber())));
					orderProduct.setProductCode(v.getProductCode());
					orderProduct.setProductName(v.getProductName());
					orderProduct.setProductImage(v.getProductImage());
					orderProduct.setAttach(v.getAttach());
					return orderProduct;
				}).collect(Collectors.toList());
				orderProductService.saveBatch(orderProductList4Insert);
			}
		}

		return null;
	}

	@Override
	public OrderDetailResp cancelByUser(@RequestBody OrderCancelReq orderCancelReq) {
		String orderCode = orderCancelReq.getOrderCode();
		LocalDateTime cancelTime = orderCancelReq.getCancelTime();
		boolean affect = orderService.cancel(orderCode, cancelTime);
		if (!affect) {
			ExceptionUtil.throwException("取消订单失败，请刷新订单");
		}

		Order order = orderService.selectByOrderCode(orderCode);
		String subOrderUrl = order.getSubOrderUrl();
		List<OrderProduct> orderProductList = orderProductService.selectByOrderCode(order.getOrderCode());

		OrderDetailResp orderDetailResp = toOrderDetailResp(order);
		Object data = requestSubOrder(OrderEnum.SubOrderEventEnum.USER_CANCEL, subOrderUrl, order, orderProductList);
		orderDetailResp.setSubOrder(data);

		return orderDetailResp;
	}

	@Override
	public Boolean cancelByTimeout(@RequestBody OrderCancelReq orderCancelReq) {
		String orderCode = orderCancelReq.getOrderCode();
		LocalDateTime cancelTime = orderCancelReq.getCancelTime();
		boolean affect = orderService.cancel(orderCode, cancelTime);
		return affect;
	}

	@Override
	public Boolean paySuccess(@RequestBody OrderPaySuccessReq orderPaySuccessReq) {
		String orderCode = orderPaySuccessReq.getOrderCode();
		BigDecimal payAmount = orderPaySuccessReq.getPayAmount();
		LocalDateTime payTime = orderPaySuccessReq.getPayTime();

		boolean affect = orderService.paySuccess(orderCode, payAmount, payTime);
		return affect;
	}

	@Override
	public Boolean receive(@RequestBody OrderReceiveReq orderReceiveReq) {
		String orderCode = orderReceiveReq.getOrderCode();
		LocalDateTime finishTime = orderReceiveReq.getFinishTime();

		boolean affect = orderService.finish(orderCode, finishTime);
		return affect;
	}

	@Override
	public Boolean finish(@RequestBody OrderFinishReq orderFinishReq) {
		String orderCode = orderFinishReq.getOrderCode();
		LocalDateTime finishTime = orderFinishReq.getFinishTime();

		boolean affect = orderService.finish(orderCode, finishTime);
		return affect;
	}

	@Override
	public OrderRefundApplyResp refundApply(@RequestBody OrderRefundApplyReq orderRefundApplyReq) {
		String orderCode = orderRefundApplyReq.getOrderCode();
		Order order = orderService.selectByOrderCode(orderCode);

		BigDecimal needPayAmount = order.getNeedPayAmount();
		BigDecimal refundAmount = order.getRefundAmount();
		if (needPayAmount.compareTo(refundAmount) <= 0) {
			ExceptionUtil.throwException("无可退款金额");
		}

		OrderRefundApplyResp resp = new OrderRefundApplyResp();
		LocalDateTime refundApplyTime = orderRefundApplyReq.getRefundApplyTime();
		boolean updateSuccess = orderService.refundApply(orderCode, refundApplyTime);
		if (!updateSuccess) {
			ExceptionUtil.throwException("当前不可申请退款，请刷新后重试！");
		}

		OrderEnum.SubStatusEnum oldSubStatus = OrderEnum.SubStatusEnum.of(order.getSubStatus());
		resp.setOldSubStatus(oldSubStatus);
		// TODO 如果有手续费之类的扣费逻辑，需要提供接口根据子订单逻辑来计算可退款金额

		List<OrderProduct> orderProductList = orderProductService.selectByOrderCode(order.getOrderCode());
		String subOrderUrl = order.getSubOrderUrl();
		Object data = requestSubOrder(OrderEnum.SubOrderEventEnum.CALC_CANREFUNDAMOUNT, subOrderUrl, order, orderProductList);
		JsonNode dataJSON = JsonUtil.toJsonNode(data);

		BigDecimal payAmount = order.getPayAmount();
		BigDecimal canRefundAmount = payAmount.subtract(refundAmount);
		if (dataJSON.has("canRefundAmount")) {
			canRefundAmount = JsonUtil.getBigDecimal(dataJSON, "canRefundAmount");
		}
		resp.setCanRefundAmount(canRefundAmount);
		String attach = JsonUtil.getString(dataJSON, "attach");
		resp.setAttach(attach);

		return resp;
	}

	@Override
	public Boolean refundFail(@RequestBody OrderRefundFailReq orderRefundFailReq) {
		String orderCode = orderRefundFailReq.getOrderCode();
		OrderEnum.SubStatusEnum oldSubStatus = orderRefundFailReq.getOldSubStatus();
		String failReason = orderRefundFailReq.getFailReason();

		boolean affect = orderService.refundFail(orderCode, oldSubStatus, failReason);
		return affect;
	}

	@Override
	public Boolean refundFinish(@RequestBody OrderRefundFinishReq orderRefundFinishReq) {
		String orderCode = orderRefundFinishReq.getOrderCode();
		LocalDateTime refundFinishTime = orderRefundFinishReq.getRefundFinishTime();
		BigDecimal totalRefundAmount = orderRefundFinishReq.getTotalRefundAmount();
		Boolean refundAll = orderRefundFinishReq.getRefundAll();

		boolean affect = orderService.refundFinish(orderCode, refundFinishTime, totalRefundAmount, refundAll);
		return affect;
	}

	@Override
	public Void deleteOrder(String orderCode) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		Order order = orderService.selectByOrderCode(orderCode);
		if (order == null) {
			ExceptionUtil.throwException("订单不存在");
		}
		if (!order.getUserId().equals(userId)) {
			ExceptionUtil.throwException("订单不匹配");
		}

		orderService.deleteOrder(orderCode);
		return null;
	}

	@Override
	public List<OrderResp> page(
			@Valid @NotNull(message = "缺少参数当前页") @Min(value = 1, message = "当前页不能小于1") Integer current,
			@Valid @NotNull(message = "缺少参数每页数量") Integer size, OrderEnum.StatusEnum status) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		Page<Order> page = new Page<>(current, size);
		List<Order> orderList = orderService.pageByUserIdAndStatus(page, userId, status);

		List<String> orderCodeList = orderList.stream().map(Order::getOrderCode).collect(Collectors.toList());
		Map<String, List<OrderProduct>> orderCodeThisListMap = orderProductService.groupByOrderCodes(orderCodeList);

		List<OrderResp> orderRespList = orderList.parallelStream().map(v -> {
			OrderResp orderResp = toOrderResp(v, orderCodeThisListMap);
			return orderResp;
		}).collect(Collectors.toList());
		return orderRespList;
	}

	private OrderResp toOrderResp(Order order, Map<String, List<OrderProduct>> orderCodeThisListMap) {
		OrderResp orderResp = new OrderResp();
		orderResp.setOrderCode(order.getOrderCode());
		String orderType = order.getOrderType();
		orderResp.setOrderType(orderType);

		List<OrderProduct> orderProductList = orderCodeThisListMap.getOrDefault(order.getOrderCode(),
				Collections.emptyList());
		List<OrderResp.ProductResp> productRespList = PropertyUtils.copyArrayProperties(orderProductList,
				OrderResp.ProductResp.class);
		orderResp.setProductList(productRespList);

		OrderEnum.StatusEnum statusEnum = OrderEnum.StatusEnum.of(order.getStatus());
		OrderEnum.SubStatusEnum subStatusEnum = OrderEnum.SubStatusEnum.of(order.getSubStatus());

		orderResp.setStatusText(subStatusEnum.getMessage());
		if (OrderEnum.SubStatusEnum.COMPLETE == subStatusEnum) {
			orderResp.setStatusText(statusEnum.getMessage());
		}

		List<OrderResp.BottonResp> bottonList = Lists.newArrayList();
		if (OrderEnum.StatusEnum.WAIT_PAY == statusEnum) {
			orderResp.setTimeText("下单时间");
			orderResp.setTime(order.getCreateTime());
			orderResp.setPayText("需付款");
			orderResp.setPayAmount(order.getNeedPayAmount());
		} else if (OrderEnum.StatusEnum.CANCELED == statusEnum) {
			orderResp.setTimeText("关闭时间");
			orderResp.setTime(order.getPayTime());
			orderResp.setPayText("需付款");
			orderResp.setPayAmount(order.getNeedPayAmount());
		} else if (OrderEnum.StatusEnum.WAIT_SEND == statusEnum || OrderEnum.StatusEnum.WAIT_RECEIVE == statusEnum) {
			orderResp.setTimeText("付款时间");
			orderResp.setTime(order.getPayTime());
			orderResp.setPayText("实付款");
			orderResp.setPayAmount(order.getPayAmount());

			bottonList.add(new OrderResp.BottonResp("退款申请", "refundApply", "orderCode=" + order.getOrderCode(), 10));
		} else if (OrderEnum.StatusEnum.COMPLETE == statusEnum) {
			orderResp.setTimeText("完成时间");
			orderResp.setTime(order.getFinishTime());
			orderResp.setPayText("实付款");
			orderResp.setPayAmount(order.getPayAmount());
			if (OrderEnum.SubStatusEnum.WAIT_REVIEW == subStatusEnum) {
				bottonList.add(new OrderResp.BottonResp("评价", "comment", "orderCode=" + order.getOrderCode(), 20));
			}

			long hours = LocalDateTimeUtil.between(order.getFinishTime(), LocalDateTime.now(), ChronoUnit.HOURS);
			if (hours < 24) {// 订单完成n小时内可以申请退款
				PayRefundApply lastPayRefundApply = payRefundApplyMapper.selectLastByOldOrderCode(order.getOrderCode());
				PayRefundApplyEnum.RefundStatus refundStatus = Optional.ofNullable(lastPayRefundApply)
						.map(v -> PayRefundApplyEnum.RefundStatus.of(v.getRefundStatus())).orElse(null);
				if (refundStatus == null || refundStatus == PayRefundApplyEnum.RefundStatus.REJECT
						|| refundStatus == PayRefundApplyEnum.RefundStatus.REFUND_FAIL) {
					// 没有退款申请记录或申请被拒或退款失败
					bottonList.add(
							new OrderResp.BottonResp("退款申请", "refundApply", "orderCode=" + order.getOrderCode(), 10));
				}
			}

			// 有退款（部分退款）
			if (order.getRefundAmount().compareTo(BigDecimal.ZERO) != 0) {
				orderResp.setStatusText(statusEnum.getMessage() + "（有退款）");
				orderResp.setTimeText("退款时间");
				orderResp.setTime(order.getRefundTime());
			}
		} else if (OrderEnum.SubStatusEnum.CHECK == subStatusEnum) {
			orderResp.setTimeText("退款申请时间");
			orderResp.setTime(order.getRefundTime());
			orderResp.setPayText("实付款");
			orderResp.setPayAmount(order.getPayAmount());
		} else if (OrderEnum.SubStatusEnum.REFUND_SUCCESS == subStatusEnum) {
			orderResp.setTimeText("退款时间");
			orderResp.setTime(order.getRefundTime());
			orderResp.setPayText("实付款");
			orderResp.setPayAmount(order.getPayAmount());
		}

		orderResp.setCancelBtn(false);
		if (OrderEnum.StatusEnum.WAIT_PAY == statusEnum) {// 待支付情况下需返回支付参数
			orderResp.setCancelBtn(true);
			bottonList.add(new OrderResp.BottonResp("去支付", "topay", "orderCode=" + order.getOrderCode(), 10));
		}

		orderResp.setDeleteBtn(false);
		if (OrderEnum.SubStatusEnum.CANCELED == subStatusEnum || OrderEnum.SubStatusEnum.COMPLETE == subStatusEnum
				|| OrderEnum.SubStatusEnum.REFUND_SUCCESS == subStatusEnum) {// 终态才可以删除订单、再来一单
			orderResp.setDeleteBtn(true);
			String firstProductCode = orderProductList.stream().map(OrderProduct::getProductCode).findFirst()
					.orElse("");
			bottonList.add(new OrderResp.BottonResp("再来一单", "onemore", "productCode=" + firstProductCode, 10));
		}

		String subOrderUrl = order.getSubOrderUrl();

		Object data = requestSubOrder(OrderEnum.SubOrderEventEnum.QUERY_ITEM, subOrderUrl, order, orderProductList);
		orderResp.setSubOrder(data);

		// 如果data里面有同名的字段，则覆盖外层的字段
		JsonNode dataJSON = JsonUtil.toJsonNode(data);
		if (dataJSON.has("statusText")) {
			orderResp.setStatusText(JsonUtil.getString(dataJSON, "statusText"));
		}
		if (dataJSON.has("timeText")) {
			orderResp.setTimeText(JsonUtil.getString(dataJSON, "timeText"));
		}
		if (dataJSON.has("time")) {
			LocalDateTime time = LocalDateTime.parse(JsonUtil.getString(dataJSON, "time"),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			orderResp.setTime(time);
		}
		if (dataJSON.has("payText")) {
			orderResp.setPayText(JsonUtil.getString(dataJSON, "payText"));
		}

		// 如果data里面有bottonList字段，则追加到外层的bottonList
		if (dataJSON.has("bottonList")) {
			JsonNode dataJSONArray = dataJSON.get("bottonList");
			for (int i = 0; i < dataJSONArray.size(); i++) {
				JsonNode textValueJSON = dataJSONArray.get(i);
				String text = JsonUtil.getString(textValueJSON, "text");
				String key = JsonUtil.getString(textValueJSON, "key");
				String params = JsonUtil.getString(textValueJSON, "params");
				Integer sort = JsonUtil.getInteger(textValueJSON, "sort");
				bottonList.add(new OrderResp.BottonResp(text, key, params, sort));
			}
		}

		bottonList.sort((a, b) -> Integer.compare(b.getSort(), a.getSort()));
		orderResp.setBottonList(bottonList);

		return orderResp;
	}

	@Override
	public OrderDetailResp detail(String orderCode) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		Order order = orderService.selectByOrderCode(orderCode);
		if (order == null) {
			ExceptionUtil.throwException("订单不存在");
		}
		if (!order.getUserId().equals(userId)) {
			ExceptionUtil.throwException("订单不匹配");
		}

		OrderDetailResp orderDetailResp = toOrderDetailResp(order);

		return orderDetailResp;
	}

	private OrderDetailResp toOrderDetailResp(Order order) {
		OrderDetailResp orderDetailResp = new OrderDetailResp();
		orderDetailResp.setOrderCode(order.getOrderCode());
		String orderType = order.getOrderType();
		orderDetailResp.setOrderType(orderType);

		List<OrderProduct> orderProductList = orderProductService.selectByOrderCode(order.getOrderCode());
		List<OrderDetailResp.ProductResp> productRespList = PropertyUtils.copyArrayProperties(orderProductList,
				OrderDetailResp.ProductResp.class);
		orderDetailResp.setProductList(productRespList);

		OrderEnum.StatusEnum statusEnum = OrderEnum.StatusEnum.of(order.getStatus());
		OrderEnum.SubStatusEnum subStatusEnum = OrderEnum.SubStatusEnum.of(order.getSubStatus());

		orderDetailResp.setStatusText(subStatusEnum.getMessage());
		if (OrderEnum.SubStatusEnum.COMPLETE == subStatusEnum) {
			orderDetailResp.setStatusText(statusEnum.getMessage());
		}

		if (OrderEnum.StatusEnum.WAIT_PAY == statusEnum) {
			orderDetailResp.setPayText("需付款");
			orderDetailResp.setPayAmount(order.getNeedPayAmount());
		} else if (OrderEnum.StatusEnum.CANCELED == statusEnum) {
			orderDetailResp.setPayText("需付款");
			orderDetailResp.setPayAmount(order.getNeedPayAmount());
		} else if (OrderEnum.StatusEnum.WAIT_SEND == statusEnum || OrderEnum.StatusEnum.WAIT_RECEIVE == statusEnum) {
			orderDetailResp.setPayText("实付款");
			orderDetailResp.setPayAmount(order.getPayAmount());
		} else if (OrderEnum.StatusEnum.COMPLETE == statusEnum) {
			orderDetailResp.setPayText("实付款");
			orderDetailResp.setPayAmount(order.getPayAmount());
		} else if (OrderEnum.SubStatusEnum.CHECK == subStatusEnum) {
			orderDetailResp.setPayText("实付款");
			orderDetailResp.setPayAmount(order.getPayAmount());
		} else if (OrderEnum.SubStatusEnum.REFUND_SUCCESS == subStatusEnum) {
			orderDetailResp.setPayText("实付款");
			orderDetailResp.setPayAmount(order.getPayAmount());
		}

		orderDetailResp.setCancelBtn(false);
		orderDetailResp.setToPayBtn(false);
		if (OrderEnum.StatusEnum.WAIT_PAY == statusEnum) {// 待支付情况下需返回支付参数
			orderDetailResp.setCancelBtn(true);
			orderDetailResp.setToPayBtn(true);
		}

		List<OrderDetailResp.TextValueResp> textValueList = Lists.newArrayList();
		textValueList.add(new TextValueResp("下单时间", LocalDateTimeUtil.formatNormal(order.getCreateTime())));
		if (OrderEnum.StatusEnum.CANCELED == statusEnum) {
			textValueList.add(new TextValueResp("关闭时间", LocalDateTimeUtil.formatNormal(order.getPayTime())));
		} else if (OrderEnum.StatusEnum.WAIT_SEND == statusEnum || OrderEnum.StatusEnum.WAIT_RECEIVE == statusEnum) {
			textValueList.add(new TextValueResp("付款时间", LocalDateTimeUtil.formatNormal(order.getPayTime())));
		} else if (OrderEnum.StatusEnum.COMPLETE == statusEnum) {
			textValueList.add(new TextValueResp("付款时间", LocalDateTimeUtil.formatNormal(order.getPayTime())));
			textValueList.add(new TextValueResp("完成时间", LocalDateTimeUtil.formatNormal(order.getFinishTime())));
			// 有退款（部分退款）
			if (order.getRefundAmount().compareTo(BigDecimal.ZERO) != 0) {
				textValueList.add(new TextValueResp("退款时间", LocalDateTimeUtil.formatNormal(order.getRefundTime())));
				textValueList.add(new TextValueResp("退款金额", order.getRefundAmount().toPlainString() + "元"));
			}
		} else if (OrderEnum.SubStatusEnum.CHECK == subStatusEnum) {
			textValueList.add(new TextValueResp("付款时间", LocalDateTimeUtil.formatNormal(order.getPayTime())));
			textValueList.add(new TextValueResp("退款申请时间", LocalDateTimeUtil.formatNormal(order.getRefundTime())));
		} else if (OrderEnum.SubStatusEnum.REFUND_SUCCESS == subStatusEnum) {
			textValueList.add(new TextValueResp("付款时间", LocalDateTimeUtil.formatNormal(order.getPayTime())));
			textValueList.add(new TextValueResp("退款时间", LocalDateTimeUtil.formatNormal(order.getRefundTime())));
			textValueList.add(new TextValueResp("退款金额", order.getRefundAmount().toPlainString() + "元"));
		}

		orderDetailResp.setTextValueList(textValueList);

		String subOrderUrl = order.getSubOrderUrl();

		Object data = requestSubOrder(OrderEnum.SubOrderEventEnum.QUERY_DETAIL, subOrderUrl, order, orderProductList);
		orderDetailResp.setSubOrder(data);

		// 如果data里面有同名的字段，则覆盖外层的字段
		JsonNode dataJSON = JsonUtil.toJsonNode(data);
		if (dataJSON.has("statusText")) {
			orderDetailResp.setStatusText(JsonUtil.getString(dataJSON, "statusText"));
		}
		if (dataJSON.has("payText")) {
			orderDetailResp.setPayText(JsonUtil.getString(dataJSON, "payText"));
		}
		// 如果data里面有textValueList字段，则追加到外层的textValueList
		if (dataJSON.has("textValueList")) {
			JsonNode dataJSONArray = dataJSON.get("textValueList");
			for (int i = 0; i < dataJSONArray.size(); i++) {
				JsonNode textValueJSON = dataJSONArray.get(i);
				String text = JsonUtil.getString(textValueJSON, "text");
				String value = JsonUtil.getString(textValueJSON, "value");
				textValueList.add(new TextValueResp(text, value));
			}
		}

		return orderDetailResp;
	}

	private Object requestSubOrder(OrderEnum.SubOrderEventEnum subOrderEvent, String subOrderUrl, Order order,
			List<OrderProduct> orderProductList) {
		Object data = null;
		if (StringUtils.isNotBlank(subOrderUrl)) {
			long start = System.currentTimeMillis();
			OrderReq orderReq = PropertyUtils.copyProperties(order, OrderReq.class);
			orderReq.setSubOrderEvent(subOrderEvent);
			orderReq.setStatus(OrderEnum.StatusEnum.of(order.getStatus()));
			orderReq.setSubStatus(OrderEnum.SubStatusEnum.of(order.getSubStatus()));

			List<OrderReq.ProductReq> productReqList = PropertyUtils.copyArrayProperties(orderProductList,
					OrderReq.ProductReq.class);
			orderReq.setProductList(productReqList);

			data = postRestTemplate(subOrderUrl, orderReq);
			log.info("subOrderUrl:{},orderReq:{},mills:{}", subOrderUrl, orderReq,
					System.currentTimeMillis() - start);
		} else {
			Map<String, String> dataMap = Maps.newHashMap();
			dataMap.put("message", "未设置子订单查询地址");
			data = dataMap;
		}
		return data;
	}

	@Autowired
	private RestTemplate restTemplate;

	private Object postRestTemplate(String url, Object paramObject) {
		String remark = null;
		log.info("请求地址:{},原参数:{},参数:{}", url, paramObject, paramObject);
		long start = System.currentTimeMillis();
		try {
            HttpEntity<Object> httpEntity = new HttpEntity<>(paramObject);
            Object result = restTemplate.postForObject(url, httpEntity, Object.class);
            log.info("{}ms,结果:{}", System.currentTimeMillis() - start, result);
            return result;
		} catch (Exception e) {
			log.error("{}ms,异常", System.currentTimeMillis() - start, e);
			remark = ExceptionUtils.getMessage(e);
		}
        Map<String, String> dataMap = Maps.newHashMap();
        dataMap.put("message", remark);
        return dataMap;
	}

	@Override
	public List<String> select4OverSendSuccess(Integer limit) {
		List<String> orderCodeList = orderService.select4OverSendSuccess(limit);
		return orderCodeList;
	}

	@Override
	public List<String> select4OverWaitReview(Integer limit) {
		List<String> orderCodeList = orderService.select4OverWaitReview(limit);
		return orderCodeList;
	}

	@Override
	public Order4Resp selectByOrderCode(String orderCode) {
		Order order = orderService.selectByOrderCode(orderCode);
		return PropertyUtils.copyProperties(order, Order4Resp.class);
	}

}
