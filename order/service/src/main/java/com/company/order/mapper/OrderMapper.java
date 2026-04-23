package com.company.order.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.order.api.enums.OrderEnum;
import com.company.order.api.enums.OrderEnum.SubStatusEnum;
import com.company.order.entity.Order;

public interface OrderMapper extends BaseMapper<Order> {

	List<Order> pageByUserIdAndStatus(Page<Order> page, @Param("userId") Integer userId,
			@Param("statusEnum") OrderEnum.StatusEnum statusEnum);

	@Select("select * from `order` where order_code = #{orderCode}")
	Order selectByOrderCode(@Param("orderCode") String orderCode);

	@Insert("INSERT INTO `order`(user_id, order_code, order_type, status, sub_status, product_amount, order_amount, reduce_amount, need_pay_amount, sub_order_url, attach)"
			+ " VALUES (#{userId}, #{orderCode}, #{orderType}, #{status}, #{subStatus}, #{productAmount}, #{orderAmount}, #{reduceAmount}, #{needPayAmount}, #{subOrderUrl}, #{attach})"
			+ " ON DUPLICATE KEY UPDATE"
			+ " status = #{status},sub_status = #{subStatus},product_amount = #{productAmount},order_amount = #{orderAmount},reduce_amount = #{reduceAmount},need_pay_amount = #{needPayAmount},sub_order_url = #{subOrderUrl},attach = #{attach}")
	Integer saveOrUpdate(Order order);

	@Select("select order_code from `order` where pay_time > #{payTimeBegin} and sub_status = #{subStatusEnum.status} order by id asc limit #{limit}")
	List<String> select4OverSendSuccess(@Param("payTimeBegin") LocalDateTime payTimeBegin,
			@Param("subStatusEnum") SubStatusEnum subStatusEnum, @Param("limit") Integer limit);

	@Select("select order_code from `order` where finish_time > #{finishTimeBegin} and sub_status = #{subStatusEnum.status} order by id asc limit #{limit}")
	List<String> select4OverWaitReview(@Param("finishTimeBegin") LocalDateTime finishTimeBegin,
			@Param("subStatusEnum") SubStatusEnum subStatusEnum, @Param("limit") Integer limit);
}
