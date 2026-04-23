package com.company.admin.controller.user.wallet;




import com.company.admin.entity.user.wallet.AliAccount;
import com.company.admin.service.user.wallet.AliAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 支付宝Controller
 * Created by JQ棣 on 2018/11/21.
 */
@Controller
public class AliAccountController {

	@Autowired
	private AliAccountService aliAccountService;

	@RequestMapping(value = "/admin/user/aliAccount/get", method = RequestMethod.POST)
	@ResponseBody
	public AliAccount get(AliAccount aliAccount) {
		return aliAccountService.get(aliAccount);
	}

	@RequestMapping(value = "/admin/user/aliAccount/save", method = RequestMethod.POST)
	@ResponseBody
	public Void save(@Validated(AliAccount.Save.class) AliAccount aliAccount) {
		aliAccountService.save(aliAccount);
		return null;
	}

	@RequestMapping(value = "/admin/user/aliAccount/remove", method = RequestMethod.POST)
	@ResponseBody
	public Void remove(AliAccount aliAccount) {
		aliAccountService.remove(aliAccount);
		return null;
	}

	@RequestMapping(value = "/admin/user/aliAccount/update", method = RequestMethod.POST)
	@ResponseBody
	public Void update(@Validated(AliAccount.Update.class) AliAccount aliAccount) {
		aliAccountService.update(aliAccount);
		return null;
	}
}
