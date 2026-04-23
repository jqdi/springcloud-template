package com.company.order.api.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.common.fallback.FallbackUtil;
import com.company.order.api.enums.OrderEnum.StatusEnum;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.request.*;
import com.company.order.api.response.Order4Resp;
import com.company.order.api.response.OrderDetailResp;
import com.company.order.api.response.OrderRefundApplyResp;
import com.company.order.api.response.OrderResp;

@Component
public class OrderFeignFallback implements FallbackFactory<OrderFeign> {

	@Override
	public OrderFeign create(final Throwable e) {
		return new OrderFeign() {
			@Override
			public Void registerOrder(RegisterOrderReq registerOrderReq) {
				return FallbackUtil.create();
			}

			@Override
			public OrderDetailResp cancelByUser(OrderCancelReq orderCancelReq) {
				return FallbackUtil.create();
			}
			
			@Override
			public Boolean cancelByTimeout(OrderCancelReq orderCancelReq) {
				return FallbackUtil.create();
			}

			@Override
			public Boolean paySuccess(OrderPaySuccessReq orderPaySuccessReq) {
				return FallbackUtil.create();
			}
			
			@Override
			public Boolean receive(OrderReceiveReq orderReceiveReq) {
				return FallbackUtil.create();
			}

			@Override
			public Boolean finish(OrderFinishReq orderFinishReq) {
				return FallbackUtil.create();
			}

			@Override
			public OrderRefundApplyResp refundApply(OrderRefundApplyReq orderRefundApplyReq) {
				return FallbackUtil.create();
			}

			@Override
			public Boolean refundFail(OrderRefundFailReq orderRefundFailReq) {
				return FallbackUtil.create();
			}

			@Override
			public Boolean refundFinish(OrderRefundFinishReq orderRefundFinishReq) {
				return FallbackUtil.create();
			}

			@Override
			public Void deleteOrder(String orderCode) {
				return FallbackUtil.create();
			}

            @Override
            public List<OrderResp> page(Integer current, Integer size, StatusEnum status) {
                return Collections.emptyList();// 降级返回空列表
            }

			@Override
			public OrderDetailResp detail(String orderCode) {
				return FallbackUtil.create();
			}

			@Override
			public List<String> select4OverSendSuccess(Integer limit) {
                return Collections.emptyList();// 降级返回空列表
			}
			
			@Override
			public List<String> select4OverWaitReview(Integer limit) {
                return Collections.emptyList();// 降级返回空列表
			}

			@Override
			public Order4Resp selectByOrderCode(String orderCode) {
				return FallbackUtil.create();
			}

		};
	}
}
