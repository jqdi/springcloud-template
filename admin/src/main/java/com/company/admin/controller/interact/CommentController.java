package com.company.admin.controller.interact;



import com.company.admin.entity.base.XSPageModel;
import com.company.admin.service.interact.CommentService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.interact.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 评论Controller
 * Created by JQ棣 on 2018/11/09.
 */
@Controller
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@RequestMapping(value = "/admin/interact/comment/list", method = RequestMethod.GET)
	@Pagination
	@ResponseBody
	public XSPageModel<Comment> list(Comment comment) {
		return commentService.listAndCount(comment);
	}
	
	/**
	 * 隐藏/展示
	 * @param comment{id,hided}
	 * @return
	 */
	@RequestMapping(value = "/admin/interact/comment/hided", method = RequestMethod.POST)
	@ResponseBody
	public Void hided(@Validated(Comment.Hided.class) Comment comment) {
		commentService.hided(comment);
		return null;
	}
	
	/**
	 * 评论评论列表
	 * @param model
	 * @param comment{relativeId}
	 * @return
	 */
	@RequestMapping(value = "/admin/interact/comment/reply", method = RequestMethod.GET)
	@Pagination
	public String reply(Model model, Comment comment) {
		model.addAttribute("list", commentService.listComment(comment));
		return "interact/commentReply";
	}
}
