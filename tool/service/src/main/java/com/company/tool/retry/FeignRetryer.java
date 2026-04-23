package com.company.tool.retry;

import cn.hutool.core.date.LocalDateTimeUtil;

import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.trace.TraceManager;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.tool.api.enums.RetryerEnum;
import com.company.tool.api.response.RetryerResp;
import com.company.tool.entity.RetryTask;
import com.company.tool.enums.RetryTaskEnum;
import com.company.tool.mapper.RetryTaskMapper;
import com.company.tool.retry.strategy.WaitStrategyBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FeignRetryer {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private AsyncTaskExecutor executor;
	@Autowired
	private RetryTaskMapper baseMapper;
	@Autowired
	private TraceManager traceManager;

	@Value("${retry.maxDays:30}")
	private Integer maxDays;

	public void call(RetryerInfo retryerInfo) {
		String feignUrl = retryerInfo.getFeignUrl();
		Object jsonParams = retryerInfo.getJsonParams();
		int increaseSeconds = retryerInfo.getIncreaseSeconds();
		int maxFailure = retryerInfo.getMaxFailure();
		RetryerEnum.WaitStrategy waitStrategy = retryerInfo.getWaitStrategy();
		if (waitStrategy == null) {
			waitStrategy = RetryerEnum.WaitStrategy.INCREMENTING;
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextDisposeTime = retryerInfo.getNextDisposeTime();
		if (nextDisposeTime == null) {
			nextDisposeTime = now;
		}

		RetryTask retryTask = new RetryTask()
				.setUrl(feignUrl)
				.setJsonParams(JsonUtil.toJsonString(jsonParams))
				.setStatus(RetryTaskEnum.Status.PRE_CALL.getCode())
				.setIncreaseSeconds(increaseSeconds)
				.setNextDisposeTime(nextDisposeTime)
				.setMaxFailure(maxFailure)
				.setFailure(0)
				.setTraceId(traceManager.get())
				.setWaitStrategy(waitStrategy.getCode());
		baseMapper.insert(retryTask);

		if (nextDisposeTime.isAfter(now)) {
			// 如果传入的nextDisposeTime在当前时间之后，说明需要延迟一段时间才开始执行，把状态修改为CALL_FAIL利用selectId4CallFail来跑
			RetryTask retryTask4Update = new RetryTask().setId(retryTask.getId())
					.setStatus(RetryTaskEnum.Status.CALL_FAIL.getCode());
			baseMapper.updateById(retryTask4Update);
			return;
		}
		callAsync(retryTask);
	}

	public void call(Integer id) {
		RetryTask retryTask = baseMapper.selectById(id);
		Optional.ofNullable(retryTask.getTraceId()).ifPresent(traceManager::put);// 重试情况下使用同一个logid方便追踪日志

		// 安全措施，防止很久之前的数据被误调用
		LocalDateTime createTime = retryTask.getCreateTime();
		long days = LocalDateTimeUtil.between(createTime, LocalDateTime.now(), ChronoUnit.DAYS);
		if (days > maxDays) {
			ExceptionUtil.throwException(id + "创建" + days + "天，超过" + maxDays + "天，不允许执行");
		}

		RetryTask retryTask4Update = new RetryTask();
		retryTask4Update.setId(id);
		retryTask4Update.setStatus(RetryTaskEnum.Status.PRE_CALL.getCode());
		baseMapper.updateById(retryTask4Update);
		callAsync(retryTask);
	}

	private void callAsync(RetryTask retryTask) {
		executor.submit(() -> call(retryTask));
	}

	private void call(RetryTask retryTask) {
		String jsonParams = retryTask.getJsonParams();
		Object paramObject = JsonUtil.toJsonNode(jsonParams);
		String remark = null;
		boolean abandonCall = false;
		log.info("调用,地址:{},参数:{}", retryTask.getUrl(), jsonParams);
		long start = System.currentTimeMillis();
		try {
			HttpEntity<Object> httpEntity = new HttpEntity<>(paramObject);
            RetryerResp retryerResp = restTemplate.postForObject(retryTask.getUrl(), httpEntity, RetryerResp.class);
            assert retryerResp != null;
            if (retryerResp.getHasResult()) {
                abandonCall = true;
                remark = Utils.rightRemark(retryTask.getRemark(), remark);

                baseMapper.retrySuccess(RetryTaskEnum.Status.CALL_SUCCESS, JsonUtil.toJsonString(retryerResp),
                        remark, retryTask.getId());
                return;
            } else {
                remark = retryerResp.getRemark();
            }
		} catch (Exception e) {
			log.error("{}ms,调用异常", System.currentTimeMillis() - start, e);
			remark = ExceptionUtils.getMessage(e);
		}
		remark = Utils.rightRemark(retryTask.getRemark(), remark);

		String waitStrategyCode = retryTask.getWaitStrategy();
		int nextSeconds = WaitStrategyBeanFactory.of(waitStrategyCode).nextSeconds(retryTask.getIncreaseSeconds(),
				retryTask.getFailure());
		LocalDateTime nextDisposeTime = retryTask.getNextDisposeTime().plusSeconds(nextSeconds);
		baseMapper.retryFail(RetryTaskEnum.Status.CALL_FAIL, nextDisposeTime, remark, retryTask.getId());
		if (abandonCall || retryTask.getFailure() + 1 >= retryTask.getMaxFailure()) {
			baseMapper.abandonRetry(RetryTaskEnum.Status.ABANDON_CALL, retryTask.getId());
		}
	}

	public List<Integer> selectId4CallFail() {
		return baseMapper.selectId4CallFail(RetryTaskEnum.Status.CALL_FAIL.getCode());
	}

}
