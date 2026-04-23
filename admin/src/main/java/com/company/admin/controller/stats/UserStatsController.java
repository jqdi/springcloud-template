package com.company.admin.controller.stats;



import com.company.admin.service.stats.UserStatsService;

import com.company.admin.entity.stats.StatsCond;

import com.company.framework.globalresponse.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * 用户统计Controller
 * Created by JQ棣 on 2018/07/05.
 */
@Controller
@RequestMapping("/admin/stats/user")
public class UserStatsController {

	@Autowired
	private UserStatsService userStatsService;

	/**
	 * 用户统计首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("summary", userStatsService.summary());
		return "stats/user_stats";
	}

	/**
	 * 用户分析统计图
	 */
	@RequestMapping(value = "/diagram", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> diagram(StatsCond statsCond) throws ParseException {
		checkCond(statsCond);
		return userStatsService.diagram(statsCond);
	}

	/**
	 * 用户性别统计
	 */
	@RequestMapping(value = "/sex", method = RequestMethod.GET)
	public String sexIndex(Model model) {
		model.addAttribute("summary", userStatsService.sexSummary());
		return "stats/sex_stats";
	}

	/**
	 * 性别分析统计图
	 */
	@RequestMapping(value = "/sex/diagram", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> sexDiagram(StatsCond statsCond) throws ParseException {
		checkCond(statsCond);
		return userStatsService.sexDiagram(statsCond);
	}

	/**
	 * 用户年龄统计
	 */
	@RequestMapping(value = "/age", method = RequestMethod.GET)
	public String ageIndex(Model model) {
		model.addAttribute("summary", userStatsService.ageSummary());
		return "stats/age_stats";
	}

	/**
	 * 年龄分析统计图
	 */
	@RequestMapping(value = "/age/diagram", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> ageDiagram(StatsCond statsCond) throws ParseException {
		checkCond(statsCond);
		return userStatsService.ageDiagram(statsCond);
	}

	/**
	 * 检查日期以及设置默认值
	 */
	private void checkCond(StatsCond statsCond) throws ParseException {
		if(statsCond.getFreq()==null) statsCond.setFreq(1);

		SimpleDateFormat sdf;
		if(statsCond.getFreq()==1){
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			sdf = new SimpleDateFormat("yyyy-MM");
		}

		if(StringUtils.isBlank(statsCond.getStartTime())||StringUtils.isBlank(statsCond.getEndTime())){
			if(statsCond.getFreq()==1){
				//最近30天
				Calendar date = Calendar.getInstance();
				statsCond.setEndTime(sdf.format(date.getTime()));
				date.add(Calendar.DATE, -29);
				statsCond.setStartTime(sdf.format(date.getTime()));
			}else{
				//当月
				Calendar date = Calendar.getInstance();
				statsCond.setEndTime(sdf.format(date.getTime()));
				date.add(Calendar.DATE, -179);
				statsCond.setStartTime(sdf.format(date.getTime()));
			}
		}else{
			//查询开始日期不能晚于结束日期
			if(sdf.parse(statsCond.getStartTime()).after(sdf.parse(statsCond.getEndTime()))){
				ExceptionUtil.throwException("查询开始日期不能大于结束日期");
			}
	
			//时间跨度不能超过36个月或366天
			if(statsCond.getFreq()==1){
				long duration = (sdf.parse(statsCond.getEndTime()).getTime() - sdf.parse(statsCond.getStartTime()).getTime()) / (24 * 60 * 60 * 1000);
				if(Math.abs(duration) > 366L){
					ExceptionUtil.throwException("日查询跨度不能超过1年");
				}
			}else{
				Calendar startDate = Calendar.getInstance();
				startDate.setTime(sdf.parse(statsCond.getStartTime()));
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(sdf.parse(statsCond.getEndTime()));
				int duration = (endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)) * 12 + endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);
				if(Math.abs(duration) > 36){
					ExceptionUtil.throwException("月查询跨度不能超过3年");
				}
			}
		}
	}

}
