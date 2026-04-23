package com.company.admin.controller.user.wallet;



import com.company.admin.entity.user.wallet.Withdrawal;
import com.company.admin.service.user.wallet.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.company.admin.annotation.Pagination;


/**
 * 提现Controller
 * Created by JQ棣 on 2018/11/23.
 */
@Controller
public class WithdrawalController {

	@Autowired
	private WithdrawalService withdrawalService;

	@RequestMapping(value = "/admin/user/withdrawal", method = RequestMethod.GET)
	@Pagination
	public String index(Model model, Withdrawal withdrawal) {
		model.addAttribute("search", withdrawal);
		model.addAttribute("pageModel", withdrawalService.listAndCount(withdrawal));
		return "finance/withdrawal";
	}

//	@RequestMapping(value = "/admin/user/withdrawal/get", method = RequestMethod.POST)
//	@ResponseBody
//	public Withdrawal get(Withdrawal withdrawal) {
//		return withdrawalService.get(withdrawal);
//	}

	@RequestMapping(value = "/admin/user/withdrawal/remove", method = RequestMethod.POST)
	@ResponseBody
	public Void remove(Withdrawal withdrawal) {
		withdrawalService.remove(withdrawal);
		return null;
	}

	@RequestMapping(value = "/admin/user/withdrawal/audit", method = RequestMethod.POST)
	@ResponseBody
	public Void audit(Withdrawal withdrawal) {
		withdrawalService.update(withdrawal);
		return null;
	}
}
