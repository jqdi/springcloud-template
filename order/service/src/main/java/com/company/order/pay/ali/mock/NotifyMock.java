package com.company.order.pay.ali.mock;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.company.framework.util.JsonUtil;
import com.company.order.pay.ali.AliConstants;
import com.company.order.pay.ali.config.AliPayProperties;
import com.company.order.pay.ali.config.AliPayProperties.PayConfig;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotifyMock {

	public static void payNotify(PayConfig config, AlipayTradeAppPayRequest request) {
		String jsonString = FileUtil.readString("classpath:pay/mock-ali-paynotify.json", Charset.forName("UTF-8"));

		AlipayTradeAppPayModel model = (AlipayTradeAppPayModel)request.getBizModel();
		jsonString = jsonString.replace("{gmt_create}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		jsonString = jsonString.replace("{subject}", model.getSubject());

		jsonString = jsonString.replace("{total_amount}", model.getTotalAmount());
		jsonString = jsonString.replace("{app_id}", config.getAppId());
		jsonString = jsonString.replace("{gmt_payment}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		jsonString = jsonString.replace("{notify_time}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		jsonString = jsonString.replace("{out_trade_no}", model.getOutTradeNo());

//		2020122822001400921446208697
		String tradeno = "20201228220014009214" + new Random().nextInt(83262531);
		jsonString = jsonString.replace("{trade_no}", tradeno);

		@SuppressWarnings("unchecked")
		Map<String, String> params = JsonUtil.toEntity(jsonString, Map.class);

		String rsaSign = null;
		String sign = null;
		try {
//			rsaSign = AlipaySignature.rsaSign(params, config.getPrivateKey(), AliConstants.CHARSET);
//			System.out.println("rsaSign:" + rsaSign);

			String signCheckContentV1 = AlipaySignature.getSignCheckContentV1(params);
			System.out.println("signCheckContentV1:" + signCheckContentV1);
//			sign = AlipaySignature.rsa256Sign(signCheckContentV1, config.getPrivateKey(), AliConstants.CHARSET);
			sign = AlipaySignature.rsaSign(signCheckContentV1, config.getPrivateKey(), AliConstants.CHARSET, AliConstants.SIGNTYPE);
			System.out.println("sign:" + sign);

		} catch (AlipayApiException e1) {
			e1.printStackTrace();
		}

//		jsonString = jsonString.replace("{sign}", rsaSign);
		jsonString = jsonString.replace("{sign}", sign);
		System.out.println("jsonString:" + jsonString);

		@SuppressWarnings("unchecked")
		Map<String, String> aliParams = JsonUtil.toEntity(jsonString, Map.class);
		System.out.println("aliParams:" + aliParams);

		AliPayProperties.PayConfig payConfig = config;
		try {
//			String signCheckContentV11 = AlipaySignature.getSignCheckContentV1(aliParams);
//			System.out.println("signCheckContentV11:" + signCheckContentV11);

			// 调用SDK验证签名
			boolean signVerified = AlipaySignature.rsaCheckV1(aliParams, payConfig.getPubKey(),
					AliConstants.CHARSET, AliConstants.SIGNTYPE);
			System.out.println("signVerified:"+signVerified);
			if (!signVerified) {
				System.out.println("验签失败");
			}
		} catch (AlipayApiException e) {
			log.error(">>>解析支付宝回调参数异常，直接返回", e);
		}

//		String result = HttpUtil.post(request.getNotifyUrl(), jsonString);
//		log.info("mock payNotify:{}", result);
	}

	public static void main(String[] args) {
		PayConfig config = new PayConfig();
		config.setAppId("213123123123");
		config.setPubKey("MIIBIjANBgk11111111BAQEFAAOCAQ8AMIIBCgKCAQEAjZC/5YeZzOPMKdT5OuI20f7DpzmPtqioSUB1111111mkUnuMmy1qmYzgbdbatOSs80HzHehc6hDAZqtRIkmVNDIdWTi8NRa0+FQmU8mlPIPtTl9eCF5QbQ6T82hvnC/iGWup3jAhv6spVU/QwOx5fwyGyu6yEdwIj0l9i4mpHfEze8cdMMYbzrey46ZWE7+dpy5vkyXle4oChx9aRQyMpew/klPH7BVEANtglptyMbT6aLo5IAV5ApiNS5fk9Z+JQnsTx5SmNi9L10JRElKK4C/xERRO9AObO5AyRYghV8xq39Up70KHO2BBj0LieVU6BbJANInJXXyWRQIDAQAB");
		config.setPrivateKey("MIIEvgIB111111111kiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCN8H5prHac+RMvB5wmcmvXnRq+Cgh1111110Y1c4Tt3RdCse9hYt9/T9YrN5khJnkFtR/2gHWc6iI79V8Sen0ntl5HBO8OgVI4pgtrDWI4TxmF6smsqvWEiZ7QVSdbhKQ2zorC/3BbvjprHsxgtAYcv+A9555+D/6RkCTHreV0jN2LHJh+zy6Ko48TFs0ULveWEEMqNrDWSO/e3dimscOVbeC2NWHNOXKmNGCiEsaLHhirh7b3okjA4qTnLFn7w1fiMoa3nc6B7wWgxPEtwC8c0jIPioXJbvR7foZkTluhvH0zbh/Pkvm+ij3mlvFXyNmBhWoYE55eaDhAgMBAAECggEAVQJrg7jSFXOrxKgHIpO2+RzTzmvN2JEJhJB2ot3MMldIX+51jSN74tm5BU49XEjXrrFiRMuSK6RswuH75cfBcbpKnzTZlTOqRjnFrB8H2T41LG2OZpuXhAG69ONfvUeDmqBnIB6zqeRS80MsSLTBCZrO8blKDsqZMlGbras8DhUqjf+kwHoZzSH0zRwCxWQHe0jvQWi+hs6OZJxL27uOvdKTTgMmsvToA3eLTh2fJwRtje7U0ZQu16md1i2DKBshRnWrcoX2H3zqsI8GXA61v/3U+LI2hohuyb5kev3I/PFgJs5Q++ADZXwiVZQU/V/wKl2zp+/mY5tDeadVt8pBEQKBgQDn4zzG7a8MGeAgeBFJOB+LmNFMP4qjo51EAEw1UQYX582Nvn8DFT3Z8BbwJU0EJgKbhc9AHchOp1h/+6Bqf9jKjB1PsoQU3aw44HrhkV7vl9pWkYmD2qHuOu2uJqHBZDK2wSgK8qqAGxKKPeGTq3pT0dWYXfhWAEGhz98sS2SJBQKBgQCcst5QvW7asQCoPyccJrizr6r3aLYM9ky7CWjOloT9V2Fp7xjObWKkU304MjQT4p1rrnfDup9Dhx2FQiTezsxTqQm5HfBUuiq22eO0rx2mhl77BrcTwOuiw4txVQAOKOuyHbBpPYx5pCYAUmUwLYkzjadmiabzrC665Ew0O4FPLQKBgQC1vkczTrSkkl80c4Zw0Zj/y103GpIpoSGwW19fZkqVT+uuTYC0xOA+QMtpmoKbXsMqa5y9Dtqgss5NOMVI0zzxuxF6FlspE3hY96+3vT0gIe9RbC3Qdvn1gqAon0SmE0cgL5GXywvQ7ecBDHERURPGQC7dhcEOfpLyJL8ws0JvlQKBgQCIU9G42uYr21ygA7mJ82Iz6fxLHU1tf4cIHkqRmUCAjFoS6NJjHaV5I63Ii1zlg/3DJlnJ7n4LKO0U95POSIpZqrHr3NEvVXw98tj5ZHTeYP8XVCNJS0l2JLwXUIUozI8qOkw1czfYyGfLVeP1A1jfgjkGq3GVg9JwBJkLxE1ezQKBgFe9wIKMnfS6UOhUMJrTQx+iAKYTzRyuHuKybkd3S5g2mvyn55HFIUG9vhq90J5fsFvai51a0EFauE0iqvECms+FhhvXQIdnC2JY1PhpfA4MxizHhen+BQil0ZdWftD1rBDJpKyCq9dyUE8p6PRqS+441111111GPFP");

		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setOutTradeNo("343242432432432423");
		model.setSubject("订单-" + model.getOutTradeNo());
		model.setTotalAmount("1");

        request.setBizModel(model);
        request.setNotifyUrl("http://localhost:7020/openapi/notify/aliPay");

		NotifyMock.payNotify(config, request);
	}

}
