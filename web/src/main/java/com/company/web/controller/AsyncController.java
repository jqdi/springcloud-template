package com.company.web.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.company.framework.util.JsonUtil;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/async")
@Slf4j
public class AsyncController {

    @Autowired
    private AsyncTaskExecutor executor;

    @GetMapping(value = "/callable")
    public Callable<Map<String, Object>> callable() {
        log.info("start");
        Callable<Map<String, Object>> callable = () -> {
            Map<String, Object> result = Maps.newHashMap();
            result.put("async", "callable");
            log.info("async");
            return result;
        };
        log.info("end");
        return callable;
    }

    @GetMapping(value = "/webAsyncTask")
    public WebAsyncTask<Map<String, Object>> webAsyncTask() {
        log.info("start");
        WebAsyncTask<Map<String, Object>> webAsyncTask = new WebAsyncTask<>(5_000, () -> {
            Map<String, Object> result = Maps.newHashMap();
            result.put("async", "normal");
            log.info("normal");
            return result;
        });
        webAsyncTask.onTimeout(() -> {
            Map<String, Object> result = Maps.newHashMap();
            result.put("async", "onTimeout");
            log.info("onTimeout");
            return result;
        });
        webAsyncTask.onCompletion(() -> {
            log.info("onCompletion");
        });
        webAsyncTask.onError(() -> {
            Map<String, Object> result = Maps.newHashMap();
            result.put("async", "onError");
            log.info("onError");
            return result;
        });
        return webAsyncTask;
    }

    @GetMapping(value = "/deferredResult")
    public DeferredResult<Map<String, Object>> deferredResult() {
        log.info("start");
        DeferredResult<Map<String, Object>> deferredResult = new DeferredResult<>(5_000L);

        Map<String, Object> result = Maps.newHashMap();
        result.put("async", "normal");
        log.info("normal");
        deferredResult.setResult(result);

        deferredResult.onTimeout(() -> {
            log.info("onTimeout");
        });
        deferredResult.onCompletion(() -> {
            log.info("onCompletion");
        });
        deferredResult.onError(e -> {
            log.info("onError", e);
        });
        return deferredResult;
    }

}
