package com.company.admin.controller.user.vip;



import com.company.admin.entity.user.vip.VipPackage;
import com.company.admin.service.user.vip.VipPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.company.admin.annotation.Pagination;


/**
 * 会员套餐Controller
 * Created by JQ棣 on 2018/11/14.
 */
@Controller
public class VipPackageController {

	@Autowired
	private VipPackageService vipPackageService;

	@RequestMapping(value = "/admin/user/vipPackage", method = RequestMethod.GET)
	@Pagination
	public String index(Model model, VipPackage vipPackage) {
		model.addAttribute("search", vipPackage);
		model.addAttribute("pageModel", vipPackageService.listAndCount(vipPackage));
		return "user/vip_package";
	}

	@RequestMapping(value = "/admin/user/vipPackage/get", method = RequestMethod.POST)
	@ResponseBody
	public VipPackage get(VipPackage vipPackage) {
		return vipPackageService.get(vipPackage);
	}

	@RequestMapping(value = "/admin/user/vipPackage/save", method = RequestMethod.POST)
	@ResponseBody
	public Void save(@Validated(VipPackage.Save.class) VipPackage vipPackage) {
		vipPackageService.save(vipPackage);
		return null;
	}

	@RequestMapping(value = "/admin/user/vipPackage/remove", method = RequestMethod.POST)
	@ResponseBody
	public Void remove(VipPackage vipPackage) {
		vipPackageService.remove(vipPackage);
		return null;
	}

	@RequestMapping(value = "/admin/user/vipPackage/update", method = RequestMethod.POST)
	@ResponseBody
	public Void update(@Validated(VipPackage.Update.class) VipPackage vipPackage) {
		vipPackageService.update(vipPackage);
		return null;
	}
}
