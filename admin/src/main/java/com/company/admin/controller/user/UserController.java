package com.company.admin.controller.user;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.company.admin.service.user.UserService;
import com.company.admin.service.user.vip.VipService;
import com.company.admin.service.user.wallet.AliAccountService;
import com.company.admin.service.user.wallet.BankCardService;
import com.company.admin.service.user.wallet.WalletService;
import com.company.admin.entity.user.User;
import com.company.admin.entity.user.UserAddress;
import com.company.admin.entity.user.wallet.AliAccount;
import com.company.admin.entity.user.wallet.BankCard;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.admin.service.user.CreditLogService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.user.CreditLog;

/**
 * @author JQ棣
 * @date 2018/10/27
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CreditLogService creditLogService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private VipService vipService;

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private AliAccountService alipayService;

    @RequestMapping(value = "/admin/user/list", method = RequestMethod.GET)
    public String index(Model model, User user) {
        if (user.getPage() == null) {
            user.setPage(1L);
        }
        model.addAttribute("search", user);
        model.addAttribute("pageModel", userService.listAndCount(user));
        return "user/user";
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.GET)
    public String create() {
        return "user/user_add";
    }

    @RequestMapping(value = "/admin/user/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(User.MobileUser.class) User user) {
        userService.save(user);
        return null;
    }

    @RequestMapping(value = "/admin/user/get", method = RequestMethod.GET)
    public String get(User user, Model model) {
        model.addAttribute("user", userService.get(user));
        return "user/user_detail";
    }

    @RequestMapping(value = "/admin/user/address", method = RequestMethod.GET)
    public String address(User user, Model model) {
        user.setAddressList(userService.userAddressList(new UserAddress(user.getId())));
        model.addAttribute("user", user);
        return "user/user_detail_address";
    }

    @RequestMapping(value = "/admin/user/follow-fans", method = RequestMethod.GET)
    public String followAndfans(User user, Model model) {
        model.addAttribute("user", user);
        return "user/user_detail_follow_fans";
    }

    @RequestMapping(value = "/admin/user/creditLog", method = RequestMethod.GET)
    @Pagination
    public String creditLog(User user, Model model) {
    	CreditLog creditLog = new CreditLog().setUserId(user.getId()).setType(user.getType());
    	creditLog.setDynamic(user.getDynamic());
    	creditLog.setOffset(user.getOffset());
    	creditLog.setLimit(user.getLimit());
    	model.addAttribute("pageModel", creditLogService.listAndCount(creditLog));
    	model.addAttribute("user", user);
    	return "user/user_detail_creditLog";
    }

    @RequestMapping(value = "/admin/user/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(User user) {
        userService.remove(user);
        return null;
    }

    @RequestMapping(value = "/admin/user/update/get", method = RequestMethod.GET)
    public String updateGet(User user, Model model) {
        model.addAttribute("user", userService.get(user));
        return "user/user_update";
    }

    @RequestMapping(value = "/admin/user/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(User.MobileUser.class) User user) {
        userService.update(user);
        return null;
    }

    @RequestMapping(value = "/admin/user/wallet", method = RequestMethod.GET)
    @Pagination
    public String userWallet(User user, Model model) {
        model.addAttribute("user", userService.get(user));
        model.addAttribute("pageModel", walletService.listHistory(user));
        return "user/user_detail_wallet";
    }

    @RequestMapping(value = "/admin/user/wallet/update", method = RequestMethod.POST)
    @ResponseBody
    public Void updatUserWallet(@Validated(User.UpdateWallet.class) User user, Model model) {
        walletService.update(user.getId(), user.getChangeFee(), user.getPlatform(), 1, null);
        model.addAttribute("user", userService.get(user));
        return null;
    }

    @RequestMapping(value = "/admin/user/vip", method = RequestMethod.GET)
    @Pagination
    public String userVip(User user, Model model) {
        model.addAttribute("user", userService.get(user));
        model.addAttribute("pageModel", vipService.listHistory(user));
        return "user/user_detail_vip";
    }

    @RequestMapping(value = "/admin/user/vip/update", method = RequestMethod.POST)
    @ResponseBody
    public Void updateUserVip(@Validated(User.UpdateVip.class) User user, Model model) {
        vipService.update(user.getId(), user.getChangeDuration());
        model.addAttribute("user", userService.get(user));
        return null;
    }

    @RequestMapping(value = "/admin/user/invite", method = RequestMethod.GET)
    @Pagination
    public String userInvite(User user, Model model) {
        User existent = userService.get(user);
        model.addAttribute("user", existent);
        if (StringUtils.isNotBlank(existent.getInviterId())) {
            User inviter = new User(existent.getInviterId());
            model.addAttribute("inviter", userService.get(inviter));
        }
        model.addAttribute("pageModel", userService.listInvitedUser(user));
        return "user/user_detail_invite";
    }

    @RequestMapping(value = "/admin/user/card", method = RequestMethod.GET)
    public String userCard(User user, Model model) {
        model.addAttribute("user", user);
        BankCard bankCard = new BankCard();
        bankCard.setUser(user);
        model.addAttribute("bankCardPageModel", bankCardService.listAndCount(bankCard));
        AliAccount alipay = new AliAccount();
        alipay.setUser(user);
        model.addAttribute("aliPayPageModel", alipayService.listAndCount(alipay));
        return "user/user_detail_card";
    }

    /**
     * 根据筛选条件导出用户信息
     */
    @RequestMapping(value = "/admin/user/export", method = RequestMethod.GET)
    @ResponseBody
    public Void export(User user, HttpServletResponse response) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("y年M月d日");
        String dateStr = sdf.format(new Date()) + "用户导出.xlsx";
        String filelName = new String(dateStr.getBytes("UTF-8"), "iso-8859-1");
        // 指定下载的文件名
        response.setHeader("Content-Disposition", "attachment;filename=" + filelName);
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        XSSFWorkbook workbook = userService.export(user);
        try {
            OutputStream output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            bufferedOutPut.flush();
            workbook.write(bufferedOutPut);
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
