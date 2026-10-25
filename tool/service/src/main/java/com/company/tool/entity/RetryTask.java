package com.company.tool.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("retry_task")
public class RetryTask extends AuditableModel<RetryTask> {
	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 回调地址
	 */
	private String url;
	/**
	 * JSON参数
	 */
	private String jsonParams;
	/**
	 * JSON结果
	 */
	private String jsonResult;
	/**
	 * 状态(1:待回调,2:回调成功(END),21.回调失败,22.放弃回调(END))
	 */
	private Integer status;
	/**
	 * 等待策略(incrementing:线性递增,fixed:固定等待,exponential:指数退避,fibonacci:斐波那契,random:随机,wechat:微信)
	 */
	private String waitStrategy;
	/**
	 * 递增秒数
	 */
	private Integer increaseSeconds;
	/**
	 * 下次处理时间
	 */
	private LocalDateTime nextDisposeTime;
	/**
	 * 最大允许失败次数
	 */
	private Integer maxFailure;
	/**
	 * 当前失败次数
	 */
	private Integer failure;
	
	/**
	 * 追踪ID
	 */
	private String traceId;
	
	/**
	 * 备注信息
	 */
	private String remark;

}