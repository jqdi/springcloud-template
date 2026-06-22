package com.company.web.controller;

import com.company.web.req.ValidNestingReq;
import com.company.web.req.ValidReq;
import com.google.common.collect.Maps;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/valid")
public class ValidController {

    // get
    @GetMapping(value = "/get-params")
    public String getparams(@NotBlank(message = "p不能为空") String p) {
        // 需结合@Validated使用
        return p;
    }

    @GetMapping(value = "/get-body-form-data")
    public Map<String, Object> getbodyformdata(@NotBlank(message = "p1不能为空") String p1, @NotBlank String p2, @NotNull Integer p3) {
        // 需结合@Validated使用
        Map<String, Object> map = Maps.newHashMap();
        map.put("p1", p1);
        map.put("p2", p2);
        map.put("p3", p3);
        return map;
    }

	@GetMapping(value = "/get-body-form-data2")
    public ValidReq getbodyformdata2(@Validated ValidReq req) {
        return req;
    }

    // post
    @PostMapping(value = "/post-params")
    public String postparams(@NotBlank(message = "p不能为空") String p) {
        // 需结合@Validated使用
        return p;
    }

	@PostMapping(value = "/post-body-form-data")
	public Map<String, Object> postbodyformdata(@NotBlank String p1, @NotEmpty String p2, @NotNull Integer p3) {
        // 需结合@Validated使用
		Map<String, Object> map = Maps.newHashMap();
		map.put("p1", p1);
		map.put("p2", p2);
		map.put("p3", p3);
		return map;
	}

	@PostMapping(value = "/post-body-form-data2")
	public ValidReq postbodyformdata2(@Validated ValidReq req) {
		return req;
	}

	@PostMapping(value = "/post-body-row2")
	public Map<String, Object> postbodyrow2(@NotBlank(message = "p不能为空") @RequestBody String param) {
		// POST /reqlog/post-body-row {"asdasd":1,"sadsaddd":" asdas dasd"} 5
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("param", param);
		return newHashMap;
	}

    @PostMapping(value = "/post-body-row3")
    public ValidNestingReq postbodyrow3(@Validated @RequestBody ValidNestingReq req) {
        return req;
    }
}
