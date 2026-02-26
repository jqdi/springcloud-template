package com.company.web.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.company.framework.util.JsonUtil;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sse")
@Slf4j
public class SSEController {

	@Autowired
	private AsyncTaskExecutor executor;

    @GetMapping(value = "/completion")
    public SseEmitter completion() {
        System.out.println("start");
//        SseEmitter emitter = new SseEmitter();// 不超时
        SseEmitter emitter = new SseEmitter(3600_000L);// 3600s超时
        emitter.onTimeout(() -> System.out.println("emitter timed out"));
        emitter.onCompletion(() -> System.out.println("emitter completed"));
        emitter.onError(e -> System.out.println("emitter error: " + e));
        executor.submit(() -> {
            try {
                emitter.send("正在思考中...");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    System.out.println("i = " + i);
                    Map<String, String> v = Maps.newHashMap();
                    v.put("v", "Hello, SSE!" + i);
                    String data = JsonUtil.toJsonString(v);
                    SseEmitter.SseEventBuilder builder = SseEmitter.event()
                            .id(String.valueOf(i))
                            .name("name" + i)
                            .comment("comment" + i)
                            .data(data);
                    emitter.send(builder);
//                    emitter.send("Hello, SSE!" + i);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    throw new RuntimeException(e);
                }
            }

            try {
                emitter.send("输出完成");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            emitter.complete();
        });
        System.out.println("end");
        return emitter;
    }
}
