package com.company.admin.controller.{module};

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.admin.annotation.Pagination;
import com.company.admin.entity.{module}.{ModelName};
import com.company.admin.service.{module}.{ModelName}Service;

@Controller
public class {ModelName}Controller {
	@Autowired
	private {ModelName}Service {modelName}Service;

	@Pagination
	@RequestMapping(value = "/admin/{module}/{modelName}/index", method = RequestMethod.GET)
	public String index(Model model, {ModelName} {modelName}) {
		if ({modelName}.getPage() == null) {
			{modelName}.setPage(1L);
		}
		model.addAttribute("search", {modelName});
		model.addAttribute("pageModel", {modelName}Service.listAndCount({modelName}));
		return "{module}/{modelName}";
	}

	@RequestMapping(value = "/admin/{module}/{modelName}/create", method = RequestMethod.GET)
	public String create(Model model, HttpServletRequest request) throws Exception {
		model.addAttribute("backUrl", request.getHeader("Referer"));
		return "{module}/{modelName}Create";
	}

	@ResponseBody
	@RequestMapping(value = "/admin/{module}/{modelName}/save", method = RequestMethod.POST)
	public Void save(@Validated({ModelName}.Save.class) {ModelName} {modelName}) {
		{modelName}Service.insert({modelName});
		return null;
	}

	@RequestMapping(value = "/admin/{module}/{modelName}/update", method = RequestMethod.GET)
	public String update(Model model, HttpServletRequest request, {ModelName} {modelName}) {
		model.addAttribute("backUrl", request.getHeader("Referer"));
		model.addAttribute("{modelName}", {modelName}Service.selectById({modelName}.getId()));
		return "{module}/{modelName}Update";
	}

	@ResponseBody
	@RequestMapping(value = "/admin/{module}/{modelName}/update", method = RequestMethod.POST)
	public Void update(@Validated({ModelName}.Update.class) {ModelName} {modelName}) {
		{modelName}Service.updateById({modelName});
		return null;
	}

	@RequestMapping(value = "/admin/{module}/{modelName}/detail", method = RequestMethod.GET)
	public String detail(Model model, HttpServletRequest request, {ModelName} {modelName}) {
		model.addAttribute("backUrl", request.getHeader("Referer"));
		model.addAttribute("{modelName}", {modelName}Service.selectById({modelName}.getId()));
		return "{module}/{modelName}Detail";
	}

	@ResponseBody
	@RequestMapping(value = "/admin/{module}/{modelName}/get", method = RequestMethod.POST)
	public {ModelName} get({ModelName} {modelName}) {
		return {modelName}Service.selectById({modelName}.getId());
	}

	@ResponseBody
	@RequestMapping(value = "/admin/{module}/{modelName}/remove", method = RequestMethod.POST)
	public Void remove({ModelName} {modelName}) {
		{modelName}Service.deleteById({modelName}.getId());
		return null;
	}
}
