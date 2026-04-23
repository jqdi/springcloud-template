package com.company.admin.controller.marketing;



import com.company.admin.service.marketing.FeedbackService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.marketing.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by JQ棣 on 11/1/17.
 */
@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @RequestMapping(value = "/admin/marketing/feedback", method = RequestMethod.GET)
    @Pagination
    public String index(Model model,Feedback feedback) {
        model.addAttribute("search", feedback);
        model.addAttribute("pageModel", feedbackService.listAndCount(feedback));
        return "marketing/feedback";
    }

    @RequestMapping(value = "/admin/marketing/feedback/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(Feedback feedback) {
        feedbackService.remove(feedback);
        return null;
    }

    @RequestMapping(value = "/admin/marketing/feedback/solve", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSolve(Feedback feedback){
        feedbackService.solve(feedback);
        return null;
    }

}
