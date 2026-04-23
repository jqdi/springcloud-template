package com.company.web.controller;


import com.company.web.service.TimeService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/cache")
@Slf4j
public class CacheController {

	@Autowired
	private TimeService timeService;

	@Cacheable(value = "getById", key = "#id")
	@GetMapping(value = "/getById")
	public String getById(String id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", id);
		return "success";
	}

	@GetMapping(value = "/time")
	public String time() {
		String time = timeService.getTime();
		return time;
	}

	@GetMapping(value = "/cachetime")
	public String cachetime() {
		String time = timeService.getCacheTime();
		return time;
	}
}
