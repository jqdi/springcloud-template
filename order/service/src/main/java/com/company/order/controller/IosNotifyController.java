package com.company.order.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;

import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import com.company.framework.util.JsonUtil;
import com.company.order.api.enums.OrderPayEnum;
import com.company.order.api.feign.IosNotifyFeign;
import com.company.order.entity.PayNotify;
import com.company.order.mapper.AliPayMapper;
import com.company.order.mapper.AliPayRefundMapper;
import com.company.order.mapper.PayNotifyMapper;
import com.company.order.messagedriven.Constants;
import com.company.order.messagedriven.strategy.StrategyConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.*;

// 待完善
@Slf4j
//@RestController
@RequestMapping(value = "/iosnotify")
public class IosNotifyController implements IosNotifyFeign {

    private static final String SERVER_URL = "https://buy.itunes.apple.com/verifyReceipt";
    private static final String SANDBOX_SERVER_URL = "https://sandbox.itunes.apple.com/verifyReceipt";

	@Autowired
	private AliPayMapper aliPayMapper;

	@Autowired
	private AliPayRefundMapper aliPayRefundMapper;

	@Autowired
	private PayNotifyMapper payNotifyMapper;

	@Autowired
	private MessageSender messageSender;
    @Autowired
    private MessagedrivenProperties messagedrivenProperties;

    @Value("${iOSPay.secretKey}")
    private String secretKey;

    @Value("${iOSPay.bundleId}")
    private String bundleId;

	@Override
	public Map<String, String> iosPayNotify(@RequestBody Map<String, String> iosParams) {
		/**
		 * <pre>
		{
			"tradeId": "2020-12-28 20:46:00",
			"fee": "1",
			"transactionId": "2020122822001400921446208697",
			"receiptData": "205546307409489920",
			"sign": "bTcikFIOpTcdY1TAv9+g4dy+7ctVpcJmW/7SxrE80ZgG1uXGvj/6JH+tMtpkIxxDl2Wzz/NRq82cACVfDO1WIjeRWhCSx/QKASBijrnLDuUP9LJxCNQlGVUjW3fk9N1GzE8NaCeedpyUHX4ldNW3T3F8t+vcQSF9v6uSucNDFJpdIo/10YT9l/x/5voV82+oFJGFEWYXhT2IyWFspcCD0Wn/5wRuoR67IWDTgzm+w+jF9++XhfNY2mTKcbfvQZCb6B6sQA/SkCLY4ErY7ZkIwlQ70JgdyR3A/adVrbzhkiwszn8B6gTkgbx6hxQSr3AU61V3PjjQfFL1JN7DCMLlTA==",
			"passbackParams": "app支付"
		}
		 * </pre>
		 */

		String notifyData = JsonUtil.toJsonString(iosParams);
		// 记录原始数据
		log.info("ios notify data:{}", notifyData);
		PayNotify payNotify = new PayNotify().setMethod(OrderPayEnum.Method.IOS.getCode()).setNotifyData(notifyData);
		payNotifyMapper.insert(payNotify);

		try {
			// 验证签名
			TreeMap<String, String> sortMap = MapUtil.sort(iosParams);
			boolean correctSignature = checkSignature(sortMap);
            if (!correctSignature) {
            	payNotifyMapper.updateRemarkById("验签失败", payNotify.getId());
                return Collections.singletonMap("value", "fail");
            }
		} catch (Exception e) {
			log.error(">>>解析回调参数异常，直接返回", e);
			payNotifyMapper.updateRemarkById(e.getMessage(), payNotify.getId());
            return Collections.singletonMap("value", "fail");
		}

        String tradeId = iosParams.get("tradeId");
        BigDecimal fee =  new BigDecimal(iosParams.get("fee")).divide(new BigDecimal("100"));
        String transactionId = iosParams.get("transactionId");
        String receipt = iosParams.get("receiptData");
        String passbackParams = iosParams.get("passbackParams");

		String tradeStatus = iosParams.get("trade_status");
		String outTradeNo = iosParams.get("out_trade_no");

		// trade_status=TRADE_SUCCESS，则认为是支付成功回调

		JsonNode responseParamNode = checkReceipt(receipt, 1);
        int status = responseParamNode.get("status").asInt();
        if (status == 21007) {
            //21007表示当前的收据为沙盒环境下收据
            //苹果在审核应用时，只会在沙盒环境购买，其产生的购买凭证，也只能连接苹果的测试验证服务器。
            //但是审核的应用又是连接线上服务器，那就需要判断苹果正式验证服务器的返回状态码，如果是21007，则再一次连接测试服务器进行验证即可。
//            xsTradeFromDB.setReal(0);
//            responseParamNode = checkReceipt(receipt, xsTradeFromDB.getReal());
//            status = responseParamNode.get("status").asInt();
        }

        if (status != 0) {
            String failureReason = JsonUtil.toJsonString(responseParamNode);
            //-1表示网络错误
            //21005，21100~21199表示苹果服务器内部错误
            //遇到这些错误码则保存票据，并使用定时任务重新发起票据校验；其他错误码则直接返回客户端
            boolean iOSServerError = (status == -1) || (status == 21005) || (status >= 21100 && status <= 21199);
            if (iOSServerError) {
//                xsTradeDao.saveIOSFailureReason(xsTradeId, passbackParams, failureReason, receipt, outId,
//                        xsTradeFromDB.getReal(), currentTime);
//                return null;
            }
            else {
//                xsTradeDao.saveFailureReason(xsTradeId, failureReason, currentTime);
//                return String.valueOf(status);
            }
        }

		// 回调数据落库
//		AliPay aliPay4Update = new AliPay().setTradeStatus(tradeStatus).setTradeNo(iosParams.get("trade_no"))
//				.setGmtPayment(iosParams.get("gmt_payment"));
//
//		Wrapper<AliPay> wrapper = new UpdateWrapper<AliPay>();
//		wrapper.eq("out_trade_no", outTradeNo);
//		wrapper.and("(trade_status is null or trade_status != {0})", AliConstants.TRADE_SUCCESS);
//		// wrapper.and(a -> a.isNull("trade_status").or().ne("trade_status",
//		// AliConstants.TRADE_SUCCESS));
//		int affect = aliPayMapper.update(aliPay4Update, wrapper);
//		if (affect == 0) {
//			// 订单回调已处理完成，无需重复处理
//			payNotifyMapper.updateRemarkById("订单回调已处理完成，无需重复处理", payNotify.getId());
//			return "success";
//		}

		// MQ异步处理
		Map<String, Object> params = Maps.newHashMap();
		params.put("outTradeNo", outTradeNo);

		params.put("time", iosParams.get("tradeId"));

		// 财务流水信息
		params.put("amount", iosParams.get("total_amount"));
		params.put("orderPayMethod", OrderPayEnum.Method.IOS.getCode());
		params.put("merchantNo", iosParams.get("seller_id"));
		params.put("tradeNo", iosParams.get("trade_no"));

		messageSender.sendNormalMessage(StrategyConstants.PAY_NOTIFY_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				Constants.QUEUE.PAY_NOTIFY.KEY);
        return Collections.singletonMap("value", "success");
	}


    private Boolean checkSignature(TreeMap<String, String> paramMap) {
        String signature = paramMap.get("sign");
        signature = aesDecrypt(signature, secretKey, secretKey);
        paramMap.remove("sign");
        return generateSignature(paramMap).equals(signature);
    }

    private String generateSignature(TreeMap<String, String> params) {
        StringBuilder paramsStr = new StringBuilder();
        Set<Map.Entry<String, String>> set = params.entrySet();
        for (Map.Entry<String, String> entry : set) {
            if (entry.getValue() != null) {
                paramsStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        String encodeStr = paramsStr.substring(0, paramsStr.length() - 1);
//		try {
//			return DigestUtils.md5Hex(encodeStr.getBytes("UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			log.error("error: ", e);
//			throw new RuntimeException(e);
//		}
        return SecureUtil.md5(encodeStr);
    }

    private static String aesDecrypt(String data, String key, String iv) {
        try {
            byte[] encryptedData = hexStr2Bytes(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(encryptedData);
            return new String(original);
        } catch (Exception e) {
            log.error("error: ", e);
            return null;
        }
    }

    private static byte[] hexStr2Bytes(String src){
        src = src.trim().replace(" ", "").toUpperCase();
        int m,n;
        int iLen=src.length()/2;
        byte[] ret = new byte[iLen];
        for (int i = 0; i < iLen; i++){
            m=i*2+1;
            n=m+1;
            ret[i] = (byte)(Integer.decode("0x"+ src.substring(i*2, m) + src.substring(m,n)) & 0xFF);
        }
        return ret;
    }

    private JsonNode checkReceipt(String receipt, Integer real) {
        Map<String, String> requestParamMap = new HashMap<>();
        requestParamMap.put("receipt-data", receipt);
        String requestParamsStr = JsonUtil.toJsonString(requestParamMap);
        String url = real.equals(1) ? SERVER_URL : SANDBOX_SERVER_URL;

        String responseParamsStr = HttpUtil.post(url, requestParamsStr);
        return JsonUtil.toJsonNode(responseParamsStr);
    }

}
