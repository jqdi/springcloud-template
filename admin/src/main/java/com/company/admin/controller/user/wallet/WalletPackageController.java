package com.company.admin.controller.user.wallet;



import com.company.admin.entity.user.wallet.WalletPackage;
import com.company.admin.service.user.wallet.WalletPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.company.admin.annotation.Pagination;


/**
 * 钱包套餐Controller
 * Created by JQ棣 on 2018/11/13.
 */
@Controller
public class WalletPackageController {

	@Autowired
	private WalletPackageService walletPackageService;

	@RequestMapping(value = "/admin/user/walletPackage", method = RequestMethod.GET)
	@Pagination
	public String index(Model model, WalletPackage walletPackage) {
		model.addAttribute("search", walletPackage);
		model.addAttribute("pageModel", walletPackageService.listAndCount(walletPackage));
		return "user/wallet_package";
	}

	@RequestMapping(value = "/admin/user/walletPackage/get", method = RequestMethod.POST)
	@ResponseBody
	public WalletPackage get(WalletPackage walletPackage) {
		return walletPackageService.get(walletPackage);
	}

	@RequestMapping(value = "/admin/user/walletPackage/save", method = RequestMethod.POST)
	@ResponseBody
	public Void save(@Validated(WalletPackage.Save.class) WalletPackage walletPackage) {
		walletPackageService.save(walletPackage);
		return null;
	}

	@RequestMapping(value = "/admin/user/walletPackage/remove", method = RequestMethod.POST)
	@ResponseBody
	public Void remove(WalletPackage walletPackage) {
		walletPackageService.remove(walletPackage);
		return null;
	}

	@RequestMapping(value = "/admin/user/walletPackage/update", method = RequestMethod.POST)
	@ResponseBody
	public Void update(@Validated(WalletPackage.Update.class) WalletPackage walletPackage) {
		walletPackageService.update(walletPackage);
		return null;
	}
}
