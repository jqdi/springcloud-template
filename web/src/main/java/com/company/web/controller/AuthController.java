package com.company.web.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import com.google.common.collect.Maps;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@GetMapping("login")
	public String login(ModelAndView view) {
		return "login";
	}

	@PostMapping("tologin")
	@ResponseBody
	public Map<String, String> tologin(@RequestBody Map<String, String> params) {
		System.out.println(params);
		Map<String, String> result = Maps.newHashMap();
		result.put("token", "111111");
		result.put("url", "/index");
		return result;
	}
}
