package com.company.user.messagedriven.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.messagedriven.BaseStrategy;
import com.company.user.entity.UserSource;
import com.company.user.service.UserSourceService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 记录来源（使用场景：引流统计、邀请奖励、地推业绩计算等业务场景）
 */
@Slf4j
@Component(StrategyConstants.SOURCERECORD_STRATEGY)
public class SourceRecordStrategy implements BaseStrategy<Map<String, Object>> {

	private static final String EXIST_VALUE = "1";

    private final Cache<String, String> cache = CacheBuilder.newBuilder()//
            .maximumSize(1000)//
            .expireAfterWrite(60, TimeUnit.SECONDS)//
            .removalListener(listener -> {
                log.info("key:{},value:{},cause:{}", listener.getKey(), listener.getValue(), listener.getCause());
            }).build();

	@Autowired
	private UserSourceService userSourceService;

	@Override
	public void doStrategy(Map<String, Object> params) {
		String source = MapUtils.getString(params, "source");
		String deviceid = MapUtils.getString(params, "deviceid");

		// 数据量可能很大，需要快速过滤重复的数据，加快处理速度
		String key = String.format("user_source:%s:%s", source, deviceid);
        String result = cache.getIfPresent(key);
        if (EXIST_VALUE.equals(result)) {
			return;
		}

		// 保存source、deviceid关联关系到DB
		String timeStr = MapUtils.getString(params, "time");
		LocalDateTime time = LocalDateTimeUtil.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		save2db(deviceid, source, time);

		// 数据量可能很大，需要快速过滤重复的数据，加快处理速度
        cache.put(key, EXIST_VALUE);
	}

	private void save2db(String deviceid, String source, LocalDateTime time) {
		// 获取deviceid最近1次记录
		UserSource lastUserSource = userSourceService.selectLastByDeviceid(deviceid);
        if (lastUserSource == null) {// 新增
            userSourceService.saveOrIgnore(deviceid, source, time);
            return;
        }
        if (time.isBefore(lastUserSource.getTime())) { // 时间小于原来的时间，说明是旧数据，不需要更新
            return;
        }
        if (source.equals(lastUserSource.getSource())) {
            return;
        }
        userSourceService.saveOrIgnore(deviceid, source, time);
	}
}
