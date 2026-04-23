package com.company.order.pay.aliactivity;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayMarketingActivityOrderCreateModel;
import com.alipay.api.domain.AlipayMarketingActivityOrderRefundModel;
import com.alipay.api.domain.RefundActivityInfo;
import com.alipay.api.domain.SaleActivityInfo;
import com.alipay.api.request.AlipayMarketingActivityOrderCreateRequest;
import com.alipay.api.request.AlipayMarketingActivityOrderRefundRequest;
import com.alipay.api.response.AlipayMarketingActivityOrderCreateResponse;
import com.alipay.api.response.AlipayMarketingActivityOrderRefundResponse;
import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.order.api.response.PayOrderQueryResp;
import com.company.order.api.response.PayRefundQueryResp;
import com.company.order.entity.AliActivityCoupon;
import com.company.order.entity.AliActivityPay;
import com.company.order.entity.AliActivityPayRefund;
import com.company.order.mapper.AliActivityCouponMapper;
import com.company.order.mapper.AliActivityPayMapper;
import com.company.order.mapper.AliActivityPayRefundMapper;
import com.company.order.pay.PayFactory;
import com.company.order.pay.aliactivity.config.AliActivityPayConfiguration;
import com.company.order.pay.aliactivity.config.AliActivityPayProperties;
import com.company.order.pay.aliactivity.config.AliActivityPayProperties.PayConfig;
import com.company.order.pay.aliactivity.mock.NotifyMock;
import com.company.order.pay.core.BasePayClient;
import com.company.order.pay.dto.PayParams;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付宝兑换券支付
 *
 * <pre>
 * 支付宝钱包支付：用户点击支付，唤起支付宝收银台后， 输入正确完整的支付密码后订单创建。
 * 所以调用支付宝的sdk并不会在支付宝创建订单，需要支付完后才会创建订单
 * </pre>
 */
@Slf4j
@Component(PayFactory.ALIACTIVITY_PAYCLIENT)
public class AliActivityPayClient extends BasePayClient {

	private static final String PAY_URL = "https://openapi.alipay.com/gateway.do";

	@Autowired
	private AliActivityPayConfiguration aliActivityPayConfiguration;
	@Autowired
	private AliActivityPayMapper aliActivityPayMapper;
	@Autowired
	private AliActivityPayRefundMapper aliActivityPayRefundMapper;
	@Autowired
	private AliActivityCouponMapper aliActivityCouponMapper;
	@Autowired
	private AsyncTaskExecutor executor;

	@Value("${template.mock.aliactivitynotify:false}")
	private Boolean mockNotify;

	/**
	 * <pre>
	 * 官方文档：https://opendocs.alipay.com/pre-open/02cs7x
	 * </pre>
	 */
	@Override
    protected Object requestPayInfo(PayParams payParams) {
		AliActivityPay aliActivityPay = aliActivityPayMapper.selectByOutOrderNo(payParams.getOutTradeNo());
		Integer aliActivityPayId = null;
		String remark = null;
		if (aliActivityPay != null) {// 已下过单
			if (StringUtils.isNotBlank(aliActivityPay.getTradeNo())) {// 已成功下过单
				return aliActivityPay.getTradeNo();
			}
			aliActivityPayId = aliActivityPay.getId();
			remark = aliActivityPay.getRemark();
		}

    	AlipayMarketingActivityOrderCreateRequest request = new AlipayMarketingActivityOrderCreateRequest();
    	AlipayMarketingActivityOrderCreateModel model = new AlipayMarketingActivityOrderCreateModel();
    	model.setBuyerId(payParams.getOpenid());
    	model.setTotalAmount(payParams.getAmount().toString());
    	model.setOutOrderNo(payParams.getOutTradeNo());

		List<SaleActivityInfo> saleActivityInfoList = JsonUtil.toList(payParams.getBody(), SaleActivityInfo.class);
		model.setSaleActivityInfoList(saleActivityInfoList);

		model.setChInfo(payParams.getProductId());
//    	model.setPromoTraceInfo(promoTraceInfo);

        request.setBizModel(model);

        PayConfig payConfig = aliActivityPayConfiguration.getPayConfig(payParams.getAppid());
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(PAY_URL,
					payConfig.getAppId(),
					payConfig.getPrivateKey(),
	                AliActivityConstants.FORMAT,
	                AliActivityConstants.CHARSET,
	                payConfig.getPubKey(),
	                AliActivityConstants.SIGNTYPE);

			AlipayMarketingActivityOrderCreateResponse response = alipayClient.execute(request);
			log.info("AlipayMarketingActivityOrderCreateResponse:{}", response);
			/**
			 * <pre>
			 * 正常结果：
			{
				"code": "10000",
				"msg": "Success",
				"body": "{\"alipay_marketing_activity_order_create_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"order_no\":\"2022122901502300000009120011010405\",\"out_order_no\":\"2131232132343232217\",\"trade_no\":\"2022122922001434911443931564\"},\"sign\":\"JLpuB7rnwaUfH32WnFhRmUFrrZVutoIPiOrFpWm4/FHi3DGDXppnaRLtTbvXnoFdSgCrRkkmsWTMlg+NpqsmzhNYb6LEPn55Lq0b/HAFy1BZDI/RgX3dY4NoP7iEAOGOJlONpHTh+otxMpAWQqqCrfODNx3+S+PI465b4DPc4ztJXsycLHwVFYKpYqA68e1ML4SbgwdkGoDr/5nRUoM7xVPJVrwgux+VfwJcSe+R5tsSJd+EhB+F81fwP7duwmza8tvmGG7LKjRdC1+EzD7JZsXxDpZfsqey1uecZ/aJ3VAzXCYkAYkTIJ2wOyhm7hp33HmoIDEYCcfnxemLmFhLcQ==\"}",
				"params": {
					"biz_content": "{\"buyer_id\":\"2088412687134912\",\"out_order_no\":\"2131232132343232217\",\"sale_activity_info_list\":[{\"activity_id\":\"2022122900826004776420383498\",\"amount\":\"1.00\",\"quantity\":1}],\"total_amount\":\"1.00\"}"
				},
				"orderNo": "2022122901502300000009120011010405",
				"outOrderNo": "2131232132343232217",
				"tradeNo": "2022122922001434911443931564",
				"success": true,
				"errorCode": "10000"
			}
			 * 异常结果：
			{
				"code": "40004",
				"msg": "Business Failed",
				"subCode": "INVALID_PARAMETER",
				"subMsg": "参数有误调用 mrchorder 下单异常，request[{\"bizScene\":\"MERCHANT_OPEN_API_SALE_VOUCHER\",\"buyer\":{\"identity\":\"2088412687134912\",\"issuer\":\"ALIPAY\",\"type\":\"USER_ID\"},\"createSystem\":\"mrchpromoprod\",\"extInfo\":{\"INPUT_TRADE_DISABLE_DETAIL_CHANNELS\":\"\",\"promoitem_pass_through_info\":\"{\\\"PROMO_SCENE_ID\\\":\\\"CB15237540\\\",\\\"POSITION_CODE\\\":\\\"GROUPON_DETAIL\\\",\\\"UPPER_SCENE_ID\\\":\\\"CB15237540\\\"}\",\"appId\":\"2021003111112476\",\"pass_through_info\":\"{\\\"mktAppId\\\":\\\"2021003111112476\\\",\\\"mktInvokeAppId\\\":\\\"2021003111112476\\\",\\\"mktInvokePid\\\":\\\"2088931411114778\\\",\\\"mktPid\\\":\\\"2088931411114778\\\",\\\"mktUserId\\\":\\\"2088412687134912\\\"}\",\"INPUT_TRADE_DISABLE_CHANNELS_SWITCH\":\"N\"},\"goodsInfoList\":[{\"displayChannel\":\"COMMON\",\"goodsType\":\"PROMO_ITEM\",\"itemId\":\"IT20221229277900710565\",\"itemName\":\"测试商家券-steve-111\",\"priceInfo\":{\"price\":{\"MONEY\":\"1.20\"}},\"purchaseType\":\"CASH_PAY\",\"quantity\":1,\"snapShotId\":\"101950295\"}],\"orderAmount\":{\"price\":{\"MONEY\":\"1.20\"}},\"outBizNo\":\"2021003111112476_2131232132343232218\",\"payToolRequestDetailList\":[{\"extInfo\":{},\"payAmount\":{\"price\":{\"MONEY\":\"1.20\"}},\"payModeEnum\":\"ASYNC\",\"payToolRequestNo\":\"2131232132343232218\",\"toolCode\":\"TRADE\"}],\"realAmount\":{\"price\":{\"MONEY\":\"1.20\"}},\"serviceType\":\"SALE_EXCHANGE_ORDER_VOUCHER\",\"timeOutRules\":{},\"title\":\"测试商家券-steve-111\"}]",
				"body": "{\"alipay_marketing_activity_order_create_response\":{\"msg\":\"Business Failed\",\"code\":\"40004\",\"sub_msg\":\"参数有误调用 mrchorder 下单异常，request[{\\\"bizScene\\\":\\\"MERCHANT_OPEN_API_SALE_VOUCHER\\\",\\\"buyer\\\":{\\\"identity\\\":\\\"2088412687134912\\\",\\\"issuer\\\":\\\"ALIPAY\\\",\\\"type\\\":\\\"USER_ID\\\"},\\\"createSystem\\\":\\\"mrchpromoprod\\\",\\\"extInfo\\\":{\\\"INPUT_TRADE_DISABLE_DETAIL_CHANNELS\\\":\\\"\\\",\\\"promoitem_pass_through_info\\\":\\\"{\\\\\\\"PROMO_SCENE_ID\\\\\\\":\\\\\\\"CB15237540\\\\\\\",\\\\\\\"POSITION_CODE\\\\\\\":\\\\\\\"GROUPON_DETAIL\\\\\\\",\\\\\\\"UPPER_SCENE_ID\\\\\\\":\\\\\\\"CB15237540\\\\\\\"}\\\",\\\"appId\\\":\\\"2021003111112476\\\",\\\"pass_through_info\\\":\\\"{\\\\\\\"mktAppId\\\\\\\":\\\\\\\"2021003111112476\\\\\\\",\\\\\\\"mktInvokeAppId\\\\\\\":\\\\\\\"2021003111112476\\\\\\\",\\\\\\\"mktInvokePid\\\\\\\":\\\\\\\"2088931411114778\\\\\\\",\\\\\\\"mktPid\\\\\\\":\\\\\\\"2088931411114778\\\\\\\",\\\\\\\"mktUserId\\\\\\\":\\\\\\\"2088412687134912\\\\\\\"}\\\",\\\"INPUT_TRADE_DISABLE_CHANNELS_SWITCH\\\":\\\"N\\\"},\\\"goodsInfoList\\\":[{\\\"displayChannel\\\":\\\"COMMON\\\",\\\"goodsType\\\":\\\"PROMO_ITEM\\\",\\\"itemId\\\":\\\"IT20221229277900710565\\\",\\\"itemName\\\":\\\"测试商家券-steve-111\\\",\\\"priceInfo\\\":{\\\"price\\\":{\\\"MONEY\\\":\\\"1.20\\\"}},\\\"purchaseType\\\":\\\"CASH_PAY\\\",\\\"quantity\\\":1,\\\"snapShotId\\\":\\\"101950295\\\"}],\\\"orderAmount\\\":{\\\"price\\\":{\\\"MONEY\\\":\\\"1.20\\\"}},\\\"outBizNo\\\":\\\"2021003111112476_2131232132343232218\\\",\\\"payToolRequestDetailList\\\":[{\\\"extInfo\\\":{},\\\"payAmount\\\":{\\\"price\\\":{\\\"MONEY\\\":\\\"1.20\\\"}},\\\"payModeEnum\\\":\\\"ASYNC\\\",\\\"payToolRequestNo\\\":\\\"2131232132343232218\\\",\\\"toolCode\\\":\\\"TRADE\\\"}],\\\"realAmount\\\":{\\\"price\\\":{\\\"MONEY\\\":\\\"1.20\\\"}},\\\"serviceType\\\":\\\"SALE_EXCHANGE_ORDER_VOUCHER\\\",\\\"timeOutRules\\\":{},\\\"title\\\":\\\"测试商家券-steve-111\\\"}]\",\"sub_code\":\"INVALID_PARAMETER\"},\"sign\":\"GXFTGdGcxJUMpvXtFHW+qbpbMw+CLUja6VX/siPcyEvMNdi5XbcTCz0IE9/tDKps5GiD3DD7kXRhVUAwDpTRx5mRSUxDJ0WtSHridT81+0wx9MhiTzwAl/yNTtt6pLMJn6Z0BRMJp8sN7N2i+Ogwee3GJyY7OH7heuDSAEp7dFLazTzM2d0BnqbojJGK7edmMojHrdrtXdV2qPT4uKvlJPaQ/1wlWjK8FBtjIxIit+VUDoaI+5Y4NB+7ptx5mMMBKo4KMYRYaiDyLKGcprZJk0S4i60J7rnt30cx25KJtYjacp0bAy2ZnRO4UYdNiJt4r75ODEA6YpACHbFsCXzppA==\"}",
				"params": {
					"biz_content": "{\"buyer_id\":\"2088412687134912\",\"out_order_no\":\"2131232132343232218\",\"sale_activity_info_list\":[{\"activity_id\":\"2022122900826004776420383498\",\"amount\":\"1.20\",\"quantity\":1}],\"total_amount\":\"1.20\"}"
				},
				"success": false,
				"errorCode": "40004"
			}
			 * </pre>
			 */
			requestResult2AliActivityPay(aliActivityPayId, payParams, request, response, remark);

			if (!response.isSuccess()) {
				throw new RuntimeException(response.getMsg());
			}

			if (mockNotify && !SpringContextUtil.isProduceProfile()) {
				executor.submit(() -> {
					try {
						Thread.sleep(10000);// 10s后回调
					} catch (InterruptedException e) {
						log.error("sleep error", e);
					}
					log.info("模拟支付回调入口");
					NotifyMock.payNotify(payConfig, request);
				});
			}

			/**
			 * <pre>
			 * 前端使用小程序支付，返回tradeNo
			 * 小程序支付服务端文档：https://opendocs.alipay.com/mini/03l5wn
			 * 小程序支付小程序端文档：https://opendocs.alipay.com/mini/api/openapi-pay
			 * </pre>
			 */
			return response.getTradeNo();
		} catch (AlipayApiException e) {
        	// 支付异常
			log.error("AliActivityPay error", e);
			remark = Utils.rightRemark(remark, "请求异常");
			requestResult2AliActivityPay(aliActivityPayId, payParams, request, null, remark);
			throw new RuntimeException(e.getErrMsg());
        } catch (Exception e) {
        	// 支付异常
			log.error("AliActivityPay error", e);
			remark = Utils.rightRemark(remark, "请求异常");
			requestResult2AliActivityPay(aliActivityPayId, payParams, request, null, remark);
			throw new RuntimeException(e.getMessage());
        }
    }

	private void requestResult2AliActivityPay(Integer aliActivityPayId, PayParams payParams,
			AlipayMarketingActivityOrderCreateRequest request, AlipayMarketingActivityOrderCreateResponse result, String remark) {
		// 保存支付宝支付数据
    	AliActivityPay aliActivityPay = new AliActivityPay()
				/* 支付参数 */
				.setAppid              (payParams.getAppid())
				.setBuyerId          	(payParams.getOpenid())
				.setOutOrderNo         (payParams.getOutTradeNo())
				.setSaleActivityInfoList(payParams.getBody())
				.setTotalAmount           (payParams.getAmount())
				;

		if (result != null) {
			/* 支付信息 */
			if (result.isSuccess()) {
				aliActivityPay.setTradeNo(result.getTradeNo());
				aliActivityPay.setOrderNo(result.getOrderNo());
				aliActivityPay.setPayBody(result.getBody());
			}
		}
		aliActivityPay.setRemark(remark);

		if (aliActivityPayId == null) {
			aliActivityPayMapper.insert(aliActivityPay);
		} else {
			aliActivityPay.setId(aliActivityPayId);
			aliActivityPayMapper.updateById(aliActivityPay);
		}
	}

	@Override
	public PayOrderQueryResp orderQuery(String outTradeNo) {
		PayOrderQueryResp resp = new PayOrderQueryResp();
		AliActivityPay aliActivityPay = aliActivityPayMapper.selectByOutOrderNo(outTradeNo);
		if (aliActivityPay == null) {
			resp.setResult(false);
			resp.setMessage("订单不存在");
			return resp;
		}

		if (AliActivityConstants.TradeStatus.payHasResult(aliActivityPay.getTradeStatus())) {
			// 回调结果
			resp.setResult(true);

			boolean paySuccess = AliActivityConstants.TradeStatus.paySuccess(aliActivityPay.getTradeStatus());
			resp.setPaySuccess(paySuccess);

			if (paySuccess) {
				resp.setPayAmount(aliActivityPay.getTotalAmount());
				LocalDateTime time = LocalDateTime.now();
				if (StringUtils.isNotBlank(aliActivityPay.getGmtPayment())) {
					time = LocalDateTime.parse(aliActivityPay.getGmtPayment(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				}
				resp.setPayTime(time);
				AliActivityPayProperties.PayConfig payConfig = aliActivityPayConfiguration.getPayConfig(aliActivityPay.getAppid());
				resp.setMerchantNo(payConfig.getSellerId());
				resp.setTradeNo(aliActivityPay.getTradeNo());
			}
			return resp;
		}

		resp.setResult(false);
		resp.setMessage("无API接口，等待回调逻辑");
		return resp;
	}

	/**
	 * <pre>
	 * 官方文档：https://opendocs.alipay.com/pre-open/02cqrz
	 * </pre>
	 */
	@Override
	public void requestRefund(String outTradeNo, String outRefundNo, BigDecimal refundAmount) {
		AliActivityPayRefund aliActivityPayRefund = aliActivityPayRefundMapper.selectByOutBizNo(outRefundNo);
		Integer aliActivityPayRefundId = null;
		if (aliActivityPayRefund != null) {// 已创建过退款记录
			if (AliActivityConstants.RefundStatus.refundSuccess(aliActivityPayRefund.getRefundStatus())) {// 已成功退款
				return;
			}
			aliActivityPayRefundId = aliActivityPayRefund.getId();
		}

		AliActivityPay aliActivityPay = aliActivityPayMapper.selectByOutOrderNo(outTradeNo);

		AlipayMarketingActivityOrderRefundRequest request = new AlipayMarketingActivityOrderRefundRequest();
		AlipayMarketingActivityOrderRefundModel model = new AlipayMarketingActivityOrderRefundModel();
		model.setOrderNo(aliActivityPay.getOrderNo());
		model.setOutBizNo(outRefundNo);// 退款订单号
		model.setBuyerId(aliActivityPay.getBuyerId());
		List<SaleActivityInfo> saleActivityInfoList = JsonUtil.toList(aliActivityPay.getSaleActivityInfoList(), SaleActivityInfo.class);

		List<RefundActivityInfo> refundActivityInfoList = Lists.newArrayList();
		for (SaleActivityInfo saleActivityInfo : saleActivityInfoList) {
			RefundActivityInfo refundActivityInfo = new RefundActivityInfo();
			refundActivityInfo.setActivityId(saleActivityInfo.getActivityId());
			refundActivityInfo.setQuantity(String.valueOf(saleActivityInfo.getQuantity()));

			// 根据activityId,buyerId查询券码
			List<AliActivityCoupon> aliActivityCouponList = aliActivityCouponMapper
					.selectByActivityIdReceiveUserId(refundActivityInfo.getActivityId(), aliActivityPay.getBuyerId());
			List<String> voucherCodeList = aliActivityCouponList.stream().map(AliActivityCoupon::getVoucherCode)
					.collect(Collectors.toList());
			refundActivityInfo.setVoucherCodeList(voucherCodeList);

			refundActivityInfoList.add(refundActivityInfo);
		}

        model.setRefundActivityInfoList(refundActivityInfoList);
        String refundActivityInfoListStr = JsonUtil.toJsonString(refundActivityInfoList);

		/**
		 * 退款类型： USER_REFUND：用户主动发起退款
		 *
		 * AUTO_EXPIRE：过期自动退款
		 */
//		model.setRefundType("USER_REFUND");
        request.setBizModel(model);
		// 支付宝退款通知url不支持动态设置，而是直接把支付时设置的通知url作为退款通知url
		// request.setNotifyUrl(notifyUrl);

		try {
			PayConfig payConfig = aliActivityPayConfiguration.getPayConfig(aliActivityPay.getAppid());
			AlipayClient alipayClient = new DefaultAlipayClient(PAY_URL,
					aliActivityPay.getAppid(),
					payConfig.getPrivateKey(),
	                AliActivityConstants.FORMAT,
	                AliActivityConstants.CHARSET,
	                payConfig.getPubKey(),
	                AliActivityConstants.SIGNTYPE);

        	refundResult2AliActivityPayRefund(aliActivityPayRefundId, aliActivityPay, outRefundNo, refundActivityInfoListStr, null);
        	AlipayMarketingActivityOrderRefundResponse response = alipayClient.execute(request);
			log.info("refund response:{}", response);
			updateRefundResult(outRefundNo, response);

			if (!response.isSuccess()) {
				throw new RuntimeException(response.getMsg());
			}
		} catch (AlipayApiException e) {
			// 退款异常
			log.error("AliActivityPayRefund error", e);
			String remark = Utils.rightRemark(aliActivityPayRefund.getRemark(), "请求异常");
			refundResult2AliActivityPayRefund(aliActivityPayRefundId, aliActivityPay, outRefundNo, refundActivityInfoListStr, remark);
			throw new RuntimeException(e.getErrMsg());
		}
	}

	private void updateRefundResult(String outRefundNo, AlipayMarketingActivityOrderRefundResponse response) {
		AliActivityPayRefund aliActivityPayRefunddb = aliActivityPayRefundMapper.selectByOutBizNo(outRefundNo);
		// 保存支付宝支付退款数据
		AliActivityPayRefund aliActivityPayRefund = new AliActivityPayRefund();
		if (response != null) {
			// 支付退款结果
			if (!response.isSuccess()) {
				String msg = StringUtils.getIfBlank(response.getSubMsg(), () -> response.getMsg());
				String remark = Utils.rightRemark(aliActivityPayRefunddb.getRemark(), msg);
				aliActivityPayRefund.setRemark(remark);
			}
		}
		aliActivityPayRefund.setId(aliActivityPayRefunddb.getId());
		aliActivityPayRefundMapper.updateById(aliActivityPayRefund);
	}

	private void refundResult2AliActivityPayRefund(Integer aliActivityPayRefundId, AliActivityPay aliActivityPay, String outRefundNo,
			String refundActivityInfoListStr, String remark) {

		// 保存支付宝支付退款数据
		AliActivityPayRefund aliActivityPayRefund = new AliActivityPayRefund()
				/* 退款参数 */
				.setAppid(aliActivityPay.getAppid())
				.setOrderNo(aliActivityPay.getOutOrderNo())
				.setOutBizNo(outRefundNo)
				.setBuyerId(aliActivityPay.getBuyerId())
				.setRefundActivityInfoList(refundActivityInfoListStr);

		aliActivityPayRefund.setRemark(remark);

		if (aliActivityPayRefundId == null) {
			aliActivityPayRefundMapper.insert(aliActivityPayRefund);
		} else {
			aliActivityPayRefund.setId(aliActivityPayRefundId);
			aliActivityPayRefundMapper.updateById(aliActivityPayRefund);
		}
	}

	@Override
	protected void requestPayCloseOrder(String outTradeNo) {
		AliActivityPay aliActivityPay = aliActivityPayMapper.selectByOutOrderNo(outTradeNo);
		if (aliActivityPay != null && StringUtils.isNotBlank(aliActivityPay.getTradeStatus())) {
			throw new RuntimeException("已支付订单不允许关闭");
		}
	}

	@Override
	public PayRefundQueryResp refundQuery(String outRefundNo) {
		PayRefundQueryResp resp = new PayRefundQueryResp();
		AliActivityPayRefund aliActivityPayRefund = aliActivityPayRefundMapper.selectByOutBizNo(outRefundNo);
		if (aliActivityPayRefund == null) {
			resp.setResult(false);
			resp.setMessage("订单不存在");
			return resp;
		}

		if (AliActivityConstants.RefundStatus.refundHasResult(aliActivityPayRefund.getRefundStatus())) {
			// 回调结果
			resp.setResult(true);

			boolean refundSuccess = AliActivityConstants.RefundStatus
					.refundSuccess(aliActivityPayRefund.getRefundStatus());
			resp.setRefundSuccess(refundSuccess);

			if (refundSuccess) {
				AliActivityPay aliActivityPay = aliActivityPayMapper
						.selectByOutOrderNo(aliActivityPayRefund.getOutOrderNo());
				resp.setRefundAmount(aliActivityPay.getTotalAmount());
				AliActivityPayProperties.PayConfig payConfig = aliActivityPayConfiguration
						.getPayConfig(aliActivityPayRefund.getAppid());
				resp.setMerchantNo(payConfig.getSellerId());
				resp.setTradeNo(aliActivityPayRefund.getOrderNo());
			}
			return resp;
		}

		resp.setResult(false);
		resp.setMessage("无API接口，等待回调逻辑");
		return resp;
	}
}
