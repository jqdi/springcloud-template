package com.company.web.controller;


import com.company.framework.util.JsonUtil;
import com.company.web.resp.OrderResp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/traceid")
@Slf4j
public class TraceIdController {

	@Autowired
	private AsyncTaskExecutor executor;

    @GetMapping(value = "/logback-arg-to-json")
    public Integer logbackArgToJson() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("aaa", "gggg");
        map.put("bbb", 123);
        map.put("ccc", null);
		OrderResp orderResp = new OrderResp();
		orderResp.setOrderCode("123");
        map.put("orderResp", orderResp);
        log.info("map:{}|{}", map, JsonUtil.toJsonString(map));

        int[] intArr = new int[] {1, 2, 3};
        log.info("intArr:{}|{}", intArr, JsonUtil.toJsonString(intArr));

        List<Integer> intList = Lists.newArrayList(1, 2, 3);
        log.info("intList:{}|{}", intList, JsonUtil.toJsonString(intList));
        return 1;
    }

	@GetMapping(value = "/thread")
	public Integer thread() {
		log.info("log begin");
		for (int i = 0; i < 5; i++) {
			int n = i;
			new Thread(() -> {
				log.info("log in thread:{}", n);
			}).start();
		}
		log.info("log end");
		return 1;
	}

	@GetMapping(value = "/threadpool")
	public Integer threadpool() {
		log.info("log begin");
		for (int i = 0; i < 5; i++) {
			int n = i;
			executor.execute(() -> {
				log.info("log in threadpool:{}", n);
			});
		}
		log.info("log end");
		return 1;
	}

	@GetMapping(value = "/threadpooltask")
	public Integer threadpooltask() {
		log.info("log begin");
		for (int i = 0; i < 5; i++) {
			int n = i;
			executor.submit(() -> {
				log.info("log in threadpool:{}", n);
			});
		}
		log.info("log end");
		return 1;
	}

	@GetMapping(value = "/forkjoin")
	public Integer forkjoin() {
		List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
		log.info("list:{}", list);

		List<String> strList = list.parallelStream()
				.map(v -> {
			log.info("log in forkjoin:{}", v);
			return v.toString();
		}).collect(Collectors.toList());

		log.info("strList:{}", strList);

		return 1;
	}

	@GetMapping(value = "/completablefuture")
	public Integer completablefuture() {
		List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
		log.info("list:{}", list);

		List<CompletableFuture<String>> list2 = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			final Integer v = list.get(i);
			CompletableFuture<String> item = CompletableFuture.supplyAsync(() -> {
				log.info("log in completablefuture:{}", v);
				return v.toString();
			});
			list2.add(item);
		}
		CompletableFuture.allOf(list2.toArray(new CompletableFuture[0])).join();
		return 1;
	}
}
