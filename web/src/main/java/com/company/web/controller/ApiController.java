package com.company.web.controller;


import com.company.token.accesscontrol.annotation.RequireLogin;
import com.company.framework.cache.ICache;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.context.SpringContextUtil;
import com.company.framework.sequence.SequenceGenerator;
import com.company.framework.threadpool.ThreadPoolProperties;
import com.company.framework.util.PropertyUtils;
import com.company.order.api.feign.OrderFeign;
import com.company.order.api.response.Order4Resp;
import com.company.order.api.response.OrderResp;
import com.company.user.api.response.UserResp;
import com.company.web.service.TimeService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

	@Autowired
	private OrderFeign orderFeign;
	@Autowired
	private AsyncTaskExecutor executor;
	@Autowired
	private TimeService timeService;

	@GetMapping(value = "/beans")
	public Map<?,?> beans() {
		ApplicationContext context = SpringContextUtil.getContext();
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		Map<String, Object> map = Maps.newHashMap();
		map.put("beanDefinitionNames", beanDefinitionNames);
		return map;
	}

	@Autowired
	private ICache cache;

	@GetMapping(value = "/timestr")
	public String timestr() {
		String string1 = cache.get("aaaaaaa", () -> {
//			int a = 1/0;
			return ""+System.currentTimeMillis();
//			return null;
		});
		System.out.println(string1);

//		String string2 = RedisUtils.get("aaaaaaa", () -> {
////			int a = 1/0;
//			return ""+System.currentTimeMillis();
//		});
//		System.out.println(string2);
		return timeService.getTime();
	}

	@Autowired
	private ThreadPoolProperties threadPoolProperties;
	@Value("${template.threadpool.maxPoolSize}")
	private Integer maxPoolSize;

    @GetMapping(value = "/info")
    public Map<String, Object> info() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("threadPoolProperties", threadPoolProperties);
        map.put("maxPoolSize", maxPoolSize);
        return map;
    }

	@RequireLogin
	@GetMapping(value = "/getOrderById")
	public Order4Resp getOrderById(String orderCode) {
//		if (true)
//			ExceptionUtil.throwException(1, "aaaaaaaaaaa");
//        OrderDetailResp detail = orderFeign.detail(orderCode);
        Order4Resp order4RespResult = orderFeign.selectByOrderCode(orderCode);
        return order4RespResult;
	}

	@GetMapping(value = "/getUserById")
	public com.company.web.resp.UserResp getUserById(Long id) {
//		UserResp byId = userFeign.getById(1L);

		UserResp userResp = new UserResp();
		userResp.setId(1L);
		userResp.setStatus(1);
		UserResp byId = userResp;
		System.out.println("byId:"+byId);
		com.company.web.resp.UserResp resp = PropertyUtils.copyProperties(byId, com.company.web.resp.UserResp.class);
		return resp;
	}

	@GetMapping(value = "/getInt")
	public Integer getInt(HttpServletRequest request) {
		String thname = Thread.currentThread().getName();
		System.out.println(thname+" "+"ApiController platform():" + HeaderContextUtil.platform());
		System.out.println(thname+" "+"ApiController httpContextHeader():" + HeaderContextUtil.httpContextHeader());
//		threadPoolTaskExecutor.submit(()->{
//			String thname2 = Thread.currentThread().getName();
//			System.err.println(thname2+" "+"ApiController submit platform():" + HeaderContextUtil.platform());
////			System.out.println(thname2+" "+"ApiController submit httpContextHeader():" + HeaderContextUtil.httpContextHeader());
//		});

		Future<Integer> submit = executor.submit(()->{
		String thname2 = Thread.currentThread().getName();
			System.err.println(thname2+" "+"ApiController submit request platform():" + request.getHeader(HeaderConstants.HEADER_PLATFORM));
			System.err.println(thname2+" "+"ApiController submit platform():" + HeaderContextUtil.platform());
	//		System.out.println(thname2+" "+"ApiController submit httpContextHeader():" + HeaderContextUtil.httpContextHeader());
		return 1;
		});

//		List<Integer> list = Lists.newArrayList(1,2,3,4);
//		List<Integer> collect = list.parallelStream().map(a->{
//			return a+1;
//		}).collect(Collectors.toList());

//		threadPoolTaskExecutor.execute(()->{
//			String thname2 = Thread.currentThread().getName();
//			System.err.println(thname2+" "+"ApiController execute platform():" + HeaderContextUtil.platform());
////			System.out.println(thname2+" "+"ApiController execute httpContextHeader():" + HeaderContextUtil.httpContextHeader());
//		});
		return 1;
	}

	@GetMapping(value = "/getString")
	public String getString() {
		return "addddddd";
	}

	@GetMapping(value = "/time")
	public Date time() {
		return new Date();
	}

	@GetMapping(value = "/onoff")
	public OrderResp onoff() {
		for (int i = 0; i < 3000; i++) {
//			OrderResp byId = orderFeign.getById((long)i);
//			log.info("onoff:{}",byId);
		}
		return new OrderResp();
	}

	@GetMapping(value = "/send")
	public String send() {
		return "{}";
	}

	@Autowired
	SequenceGenerator sequenceGenerator;

	@GetMapping(value = "/retryGet")
	public OrderResp retryGet(Long id) {
		log.info("retryGet");
//		OrderResp byId = orderFeign.retryGet(id);
//		log.info("retryGet:{}", byId);
//		return byId;
		return null;
	}

	@GetMapping(value = "/retryPost")
	public OrderResp retryPost(Long id) {
		log.info("retryPost");
//		OrderResp byId = orderFeign.retryPost(new OrderReq().setId(id));
//		log.info("retryPost:{}", byId);
//		return byId;
		return null;
	}

	@GetMapping(value = "/threadpool")
	public Integer threadpool() {
		System.out.println("executor:"+executor);
		for (int i = 0; i < 3; i++) {
			int n = i;
			executor.submit(() -> {
				System.out.println("execute:"+n);
				try {
					Thread.sleep(new Random().nextInt(1000));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			System.out.println("submit:"+n);
		}
		return 1;
	}

	@GetMapping(value = "/threadpooltask")
	public Integer threadpooltask() {
		System.out.println("executor:"+executor);
		for (int i = 0; i < 3; i++) {
			int n = i;
			executor.execute(() -> {
				System.out.println("execute:"+n);
				try {
					Thread.sleep(new Random().nextInt(1000));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			System.out.println("submit:"+n);
		}

//		executor.getActiveCount();
		return 1;
	}

	@GetMapping(value = "/log")
	public Integer log() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			try{
				int j = i/0;
				System.out.println(j);
			}catch(Exception e){
				log.error("log error", e);
			}
			log.info("log:{}", i);
		}
		long end = System.currentTimeMillis();
		System.out.println("cost:"+(end-start));
		log.info("cost:{}", (end-start));
		return 1;
	}
}
