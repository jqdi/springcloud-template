package com.company.framework.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;

public class RetryUtils {
	private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

	public static <T> T retry(Callable<T> callable, T equalToRetry, long incrementMilliseconds, int attemptNumber) {
		Retryer<T> retryer = RetryerBuilder.<T> newBuilder() //
				.retryIfResult(Predicates.equalTo(equalToRetry)) // 返回false时重试
//				.retryIfExceptionOfType(IOException.class) // 抛出IOException时重试
				.retryIfException() // 有异常时重试
				.withWaitStrategy(WaitStrategies.incrementingWait(incrementMilliseconds, TimeUnit.MILLISECONDS, incrementMilliseconds,
						TimeUnit.MILLISECONDS)) // 增量重试
				.withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber < 1 ? 1 : attemptNumber)) // 重试attemptNumber次后停止
				.withRetryListener(new RetryListener() { // 重试监听器，可记录重试情况
					@Override
					@SuppressWarnings("hiding")
					public <T> void onRetry(Attempt<T> attempt) {
						long attemptNumber = attempt.getAttemptNumber();
						long delaySinceFirstAttempt = attempt.getDelaySinceFirstAttempt();
						boolean hasException = attempt.hasException();
						if (hasException) {
							logger.error("retry {} times,delaySinceFirstAttempt:{},has error", attemptNumber,
									delaySinceFirstAttempt, attempt.getExceptionCause());
						} else {
							T result = attempt.getResult();
							logger.info("retry {} times,delaySinceFirstAttempt:{},result:{}", attemptNumber,
									delaySinceFirstAttempt, result);
						}
					}
				}).build();
		try {
			return retryer.call(callable);
		} catch (RetryException e) {
			logger.error("retry error", e);
		} catch (ExecutionException e) {
			logger.error("execution error", e);
		}
		return null;
	}

	public static <T> T retry(Callable<T> callable, T equalToRetry, int attemptNumber) {
		return retry(callable, equalToRetry, 200L, attemptNumber);
	}
}
