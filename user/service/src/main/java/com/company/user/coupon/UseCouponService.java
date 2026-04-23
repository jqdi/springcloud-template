package com.company.user.coupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.user.coupon.dto.MatchResult;
import com.company.user.coupon.dto.UserCouponCanUse;
import com.company.user.coupon.dto.UserCouponCanUseBatch;
import com.company.user.coupon.dto.UserCouponCanUseParam;
import com.company.user.coupon.dto.UserCouponCanUsePay;
import com.company.user.coupon.dto.UserCouponMe;
import com.company.user.coupon.util.CollectionUtil;
import com.company.user.entity.CouponTemplateCondition;
import com.company.user.entity.UserCoupon;
import com.company.user.service.market.CouponTemplateConditionService;
import com.company.user.service.market.UserCouponService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UseCouponService {

	@Autowired
	private UserCouponService userCouponService;
	@Autowired
	private CouponTemplateConditionService couponTemplateConditionService;

	/**
	 * <pre>
	 * 用户优惠券列表（未使用、即将过期、已使用、已过期）
	 * 应用场景：用户查看自己的优惠券列表
	 * </pre>
	 *
	 * @param userId
	 * @param status
	 * @param seeRuntimeAttach
	 * @return
	 */
	public List<UserCouponMe> listCouponByAppUserId(Integer userId, String status,
			Map<String, String> seeRuntimeAttach) {
		log.info("userId:{},status:{},seeRuntimeAttach:{}", userId, status, seeRuntimeAttach);
		List<UserCoupon> userCouponList = userCouponService.selectByUserIdStatus(userId, status);

		Set<Integer> couponTemplateIdSet = userCouponList.stream().map(UserCoupon::getCouponTemplateId)
				.collect(Collectors.toSet());

		List<CouponTemplateCondition> couponTemplateConditionBatchList = couponTemplateConditionService
				.selectByCouponTemplateIds(couponTemplateIdSet);

		Map<Integer, List<CouponTemplateCondition>> couponTemplateIdConditionMap = couponTemplateConditionBatchList
				.stream().collect(Collectors.groupingBy(CouponTemplateCondition::getCouponTemplateId));

		List<UserCoupon> userCouponCanSeeList = this.canSee(userCouponList, couponTemplateIdConditionMap, userId,
				seeRuntimeAttach);

		List<UserCouponMe> userCouponMeList = userCouponCanSeeList.stream().map(userCoupon -> {
			UserCouponMe userCouponMe = new UserCouponMe();
			userCouponMe.setUserCouponId(userCoupon.getId());
			userCouponMe.setName(userCoupon.getName());
			userCouponMe.setMaxAmount(userCoupon.getMaxAmount());
			userCouponMe.setDiscount(userCoupon.getDiscount());
			userCouponMe.setConditionAmount(userCoupon.getConditionAmount());
			userCouponMe.setEndTime(userCoupon.getEndTime());
			return userCouponMe;
		}).filter(v -> v != null).collect(Collectors.toList());

		return userCouponMeList;
	}

	/**
	 * <pre>
	 * 用户优惠券列表
	 * 应用场景：支付时展示选择优惠券的列表
	 * </pre>
	 *
	 * @param userId
	 * @param orderAmount
	 * @param runtimeAttach
	 * @return
	 */
	public List<UserCouponCanUsePay> listCouponCanUseByAppUserId(Integer userId, BigDecimal orderAmount,
			Map<String, String> runtimeAttach) {
		log.info("userId:{},runtimeAttach:{}", userId, runtimeAttach);
		String status = "nouse";
		List<UserCoupon> userCouponList = userCouponService.selectByUserIdStatus(userId, status);

		Set<Integer> couponTemplateIdSet = userCouponList.stream().map(UserCoupon::getCouponTemplateId)
				.collect(Collectors.toSet());

		List<CouponTemplateCondition> couponTemplateConditionBatchList = couponTemplateConditionService
				.selectByCouponTemplateIds(couponTemplateIdSet);

		Map<Integer, List<CouponTemplateCondition>> couponTemplateIdConditionMap = couponTemplateConditionBatchList
				.stream().collect(Collectors.groupingBy(CouponTemplateCondition::getCouponTemplateId));

		List<UserCoupon> userCouponCanSeeList = this.canSee(userCouponList, couponTemplateIdConditionMap, userId,
				runtimeAttach);

		List<UserCouponCanUsePay> userCouponCanUsePayList = userCouponCanSeeList.stream().map(userCoupon -> {
			List<CouponTemplateCondition> couponTemplateConditionList = couponTemplateIdConditionMap
					.get(userCoupon.getCouponTemplateId());
			UserCouponCanUse userCouponCanUse = this.canUse(userCoupon, couponTemplateConditionList, userId,
					orderAmount, runtimeAttach);

			UserCouponCanUsePay userCouponCanUsePay = new UserCouponCanUsePay();
			userCouponCanUsePay.setUserCouponId(userCoupon.getId());
			userCouponCanUsePay.setName(userCoupon.getName());
			userCouponCanUsePay.setMaxAmount(userCoupon.getMaxAmount());
			userCouponCanUsePay.setDiscount(userCoupon.getDiscount());
			userCouponCanUsePay.setConditionAmount(userCoupon.getConditionAmount());
			userCouponCanUsePay.setEndTime(userCoupon.getEndTime());
			userCouponCanUsePay.setCanUse(userCouponCanUse.getCanUse());
			userCouponCanUsePay.setReduceAmount(userCouponCanUse.getReduceAmount());
			userCouponCanUsePay.setReason(userCouponCanUse.getReason());
			return userCouponCanUsePay;
		}).sorted((a, b) -> {
			// 先按金额倒序，金额一致按过期时间正序
			int compareTo = b.getReduceAmount().compareTo(a.getReduceAmount());
			if (compareTo == 0) {
				compareTo = a.getEndTime().compareTo(b.getEndTime());
			}
			return compareTo;
		}).collect(Collectors.toList());

		return userCouponCanUsePayList;
	}

	/**
	 * <pre>
	 * 用户最优的可用优惠券
	 * 应用场景：支付时自动为用户选择优惠券
	 * </pre>
	 *
	 * @param userId
	 * @param orderAmount
	 * @param runtimeAttach
	 * @return
	 */
	public UserCouponCanUse bestCouponCanUse(Integer userId, BigDecimal orderAmount,
			Map<String, String> runtimeAttach) {
		log.info("userId:{},runtimeAttach:{}", userId, runtimeAttach);
		String status = "nouse";
		List<UserCoupon> userCouponList = userCouponService.selectByUserIdStatus(userId, status);

		Set<Integer> couponTemplateIdSet = userCouponList.stream().map(UserCoupon::getCouponTemplateId)
				.collect(Collectors.toSet());

		List<CouponTemplateCondition> couponTemplateConditionBatchList = couponTemplateConditionService
				.selectByCouponTemplateIds(couponTemplateIdSet);

		Map<Integer, List<CouponTemplateCondition>> couponTemplateIdConditionMap = couponTemplateConditionBatchList
				.stream().collect(Collectors.groupingBy(CouponTemplateCondition::getCouponTemplateId));

		List<UserCoupon> userCouponCanSeeList = this.canSee(userCouponList, couponTemplateIdConditionMap, userId,
				runtimeAttach);

		// 先按优惠紧急对优惠券进行排序，接下来查询到第一张满足条件的优惠券就行
		Collections.sort(userCouponCanSeeList, (a, b) -> {
			// 先按优惠金额倒序，金额一致按过期时间正序
			BigDecimal reduceAmountA = a.getMaxAmount();
			BigDecimal discountA = a.getDiscount();
			if (discountA.compareTo(BigDecimal.ONE) < 0) {// 折扣
				BigDecimal discountAmount = orderAmount.multiply(BigDecimal.ONE.subtract(discountA));
				reduceAmountA = discountAmount.min(reduceAmountA);
			}

			BigDecimal reduceAmountB = b.getMaxAmount();
			BigDecimal discountB = b.getDiscount();
			if (discountB.compareTo(BigDecimal.ONE) < 0) {// 折扣
				BigDecimal discountAmount = orderAmount.multiply(BigDecimal.ONE.subtract(discountB));
				reduceAmountB = discountAmount.min(reduceAmountB);
			}

			int compareTo = reduceAmountB.compareTo(reduceAmountA);
			if (compareTo == 0) {
				compareTo = a.getEndTime().compareTo(b.getEndTime());
			}
			return compareTo;
		});

		UserCouponCanUse bestCouponCanUse = null;
		for (UserCoupon userCoupon : userCouponCanSeeList) {
			List<CouponTemplateCondition> couponTemplateConditionList = couponTemplateIdConditionMap
					.get(userCoupon.getCouponTemplateId());
			UserCouponCanUse userCouponCanUse = this.canUse(userCoupon, couponTemplateConditionList, userId,
					orderAmount, runtimeAttach);
			if (userCouponCanUse.getCanUse()) {
				bestCouponCanUse = userCouponCanUse;
				break;
			}
		}
		return bestCouponCanUse;
	}

	/**
	 * <pre>
	 * 用户最优的可用优惠券(批量)
	 * 应用场景：商品展示券后价
	 * </pre>
	 *
	 * @param userId
	 * @param seeRuntimeAttach
	 * @param userCouponCanUseParamList
	 * @return
	 */
	public List<UserCouponCanUseBatch> bestCouponCanUseBatch(Integer userId, Map<String, String> seeRuntimeAttach,
			List<UserCouponCanUseParam> userCouponCanUseParamList) {
		log.info("userId:{},seeRuntimeAttach:{},userCouponCanUseParamList:{}", userId,
				seeRuntimeAttach, userCouponCanUseParamList);
		String status = "nouse";
		List<UserCoupon> userCouponList = userCouponService.selectByUserIdStatus(userId, status);

		Set<Integer> couponTemplateIdSet = userCouponList.stream().map(UserCoupon::getCouponTemplateId)
				.collect(Collectors.toSet());

		List<CouponTemplateCondition> couponTemplateConditionBatchList = couponTemplateConditionService
				.selectByCouponTemplateIds(couponTemplateIdSet);

		Map<Integer, List<CouponTemplateCondition>> couponTemplateIdConditionMap = couponTemplateConditionBatchList
				.stream().collect(Collectors.groupingBy(CouponTemplateCondition::getCouponTemplateId));

		List<UserCoupon> userCouponCanSeeList = this.canSee(userCouponList, couponTemplateIdConditionMap, userId,
				seeRuntimeAttach);

		List<UserCouponCanUseBatch> userCouponCanUseBatchList = userCouponCanUseParamList.stream()
				.map(userCouponCanUseParam -> {
					UserCouponCanUseBatch userCouponCanUseBatch = new UserCouponCanUseBatch();
					userCouponCanUseBatch.setUniqueCode(userCouponCanUseParam.getUniqueCode());

					// 先按优惠紧急对优惠券进行排序，接下来查询到第一张满足条件的优惠券就行
					Collections.sort(userCouponCanSeeList, (a, b) -> {
						// 先按优惠金额倒序，金额一致按过期时间正序
						BigDecimal reduceAmountA = a.getMaxAmount();
						BigDecimal discountA = a.getDiscount();
						if (discountA.compareTo(BigDecimal.ONE) < 0) {// 折扣
							BigDecimal discountAmount = userCouponCanUseParam.getOrderAmount().multiply(BigDecimal.ONE.subtract(discountA));
							reduceAmountA = discountAmount.min(reduceAmountA);
						}

						BigDecimal reduceAmountB = b.getMaxAmount();
						BigDecimal discountB = b.getDiscount();
						if (discountB.compareTo(BigDecimal.ONE) < 0) {// 折扣
							BigDecimal discountAmount = userCouponCanUseParam.getOrderAmount().multiply(BigDecimal.ONE.subtract(discountB));
							reduceAmountB = discountAmount.min(reduceAmountB);
						}

						int compareTo = reduceAmountB.compareTo(reduceAmountA);
						if (compareTo == 0) {
							compareTo = a.getEndTime().compareTo(b.getEndTime());
						}
						return compareTo;
					});

					UserCouponCanUse bestCouponCanUse = null;
					for (UserCoupon userCoupon : userCouponCanSeeList) {
						List<CouponTemplateCondition> couponTemplateConditionList = couponTemplateIdConditionMap
								.get(userCoupon.getCouponTemplateId());
						UserCouponCanUse userCouponCanUse = this.canUse(userCoupon, couponTemplateConditionList, userId,
								userCouponCanUseParam.getOrderAmount(), userCouponCanUseParam.getRuntimeAttach());
						if (userCouponCanUse.getCanUse()) {
							bestCouponCanUse = userCouponCanUse;
							break;
						}
					}

					userCouponCanUseBatch.setUserCouponCanUse(bestCouponCanUse);

					return userCouponCanUseBatch;
				}).collect(Collectors.toList());

		return userCouponCanUseBatchList;
	}

	/**
	 * <pre>
	 * 判断优惠券能否使用
	 * 应用场景：创建订单时校验优惠券
	 * </pre>
	 *
	 * @param userCouponId
	 * @param userId
	 * @param orderAmount
	 * @param runtimeAttach
	 * @return
	 */
	public UserCouponCanUse canUse(Integer userCouponId, Integer userId, BigDecimal orderAmount,
			Map<String, String> runtimeAttach) {
		UserCoupon userCoupon = userCouponService.getById(userCouponId);
		if (userCoupon == null) {
			UserCouponCanUse userCouponCanUse = new UserCouponCanUse();
			userCouponCanUse.setUserCouponId(userCouponId);
			userCouponCanUse.setReduceAmount(BigDecimal.ZERO);
			userCouponCanUse.setCanUse(false);
			userCouponCanUse.setReason("优惠券不存在");
			return userCouponCanUse;
		}

		List<CouponTemplateCondition> couponTemplateConditionList = couponTemplateConditionService
				.selectByCouponTemplateId(userCoupon.getCouponTemplateId());

		return canUse(userCoupon, couponTemplateConditionList, userId, orderAmount, runtimeAttach);
	}

	private List<UserCoupon> canSee(List<UserCoupon> userCouponList,
			Map<Integer, List<CouponTemplateCondition>> couponTemplateIdConditionMap, Integer userId,
			Map<String, String> seeRuntimeAttach) {

		List<UserCoupon> userCouponCanSeeList = userCouponList.stream().map(userCoupon -> {
			Integer userCouponId = userCoupon.getId();

			List<CouponTemplateCondition> couponTemplateConditionList = couponTemplateIdConditionMap
					.get(userCoupon.getCouponTemplateId());

			if (CollectionUtils.isEmpty(couponTemplateConditionList)) {
				log.warn("优惠券模板{}使用条件未配置", userCoupon.getCouponTemplateId());
				return null;
			}

			/*
			 * 并发执行条件判断，任意1个匹配false则返回false，否则返回true
			 */
			List<Supplier<Boolean>> supplierList = couponTemplateConditionList.stream().map(v -> {
				Supplier<Boolean> supplier = () -> {
					String beanName = v.getUseCondition();
					UseCondition condition = SpringContextUtil.getBean(beanName, UseCondition.class);
					if (condition == null) {
						log.warn("使用条件未配置:{}", beanName);
						return false;
					}
					SeeParam seeParam = SeeParam.builder().userCouponId(userCouponId).userId(userId)
							.runtimeAttach(seeRuntimeAttach).useConditionValue(v.getUseConditionValue()).build();
					boolean canSee = false;
					try {
						canSee = condition.canSee(seeParam);
					} catch (Exception e) {
						// 异常情况下不可见
						log.error("canSee error", e);
					}
					return canSee;
				};
				return supplier;
			}).collect(Collectors.toList());

			// 任意1个匹配false则返回false，否则返回true
			Boolean result = Utils.anyMatch(supplierList, v -> v == false, true);
			if (!result) {
				return null;
			}
			return userCoupon;
		}).filter(v -> v != null).collect(Collectors.toList());
		log.info("canSee size:{}->{}", userCouponList.size(), userCouponCanSeeList.size());
		return userCouponCanSeeList;
	}

	private UserCouponCanUse canUse(UserCoupon userCoupon, List<CouponTemplateCondition> couponTemplateConditionList,
			Integer userId, BigDecimal orderAmount, Map<String, String> runtimeAttach) {
		UserCouponCanUse userCouponCanUse = new UserCouponCanUse();
		userCouponCanUse.setUserCouponId(userCoupon.getId());
		userCouponCanUse.setEndTime(userCoupon.getEndTime());

		BigDecimal reduceAmount = userCoupon.getMaxAmount();
		BigDecimal discount = userCoupon.getDiscount();
		if (discount.compareTo(BigDecimal.ONE) < 0) {// 折扣
			BigDecimal discountAmount = orderAmount.multiply(BigDecimal.ONE.subtract(discount));
			reduceAmount = discountAmount.min(reduceAmount);
		}
		userCouponCanUse.setReduceAmount(reduceAmount);

		if (CollectionUtils.isEmpty(couponTemplateConditionList)) {
			log.warn("优惠券模板{}使用条件未配置", userCoupon.getCouponTemplateId());
			userCouponCanUse.setCanUse(false);
			userCouponCanUse.setReason("优惠券模板" + userCoupon.getCouponTemplateId() + "使用条件未配置");
			return userCouponCanUse;
		}

		/*
		 * 并发执行条件判断，任意1个匹配false则返回false，否则返回true
		 */
		List<Supplier<MatchResult>> supplierList = couponTemplateConditionList.stream().map(v -> {
			Supplier<MatchResult> supplier = () -> {
				String beanName = v.getUseCondition();
				UseCondition condition = SpringContextUtil.getBean(beanName, UseCondition.class);
				if (condition == null) {
					log.warn("使用条件未配置:{}", beanName);
					return MatchResult.builder().canUse(false).reason("使用条件未配置").build();
				}

				UseParam.UserCouponInfo userCouponInfo = UseParam.UserCouponInfo.builder()
						.couponTemplateId(userCoupon.getCouponTemplateId()).userId(userCoupon.getUserId())
						.status(userCoupon.getStatus()).conditionAmount(userCoupon.getConditionAmount())
						.beginTime(userCoupon.getBeginTime()).endTime(userCoupon.getEndTime()).build();
				UseParam useParam = UseParam.builder().userCouponId(userCoupon.getId()).userCouponInfo(userCouponInfo)
						.userId(userId).orderAmount(orderAmount).runtimeAttach(runtimeAttach)
						.useConditionValue(v.getUseConditionValue()).build();

				MatchResult canUse;
				try {
					canUse = condition.canUse(useParam);
				} catch (Exception e) {
					// 异常情况下不可用
					log.error("canUse error", e);
					canUse = MatchResult.builder().canUse(false).reason("系统判断异常").build();
				}
				return canUse;
			};
			return supplier;
		}).collect(Collectors.toList());

		// 任意1个匹配false则返回false，否则返回true
		MatchResult result = Utils.anyMatch(supplierList, v -> v.getCanUse() == false,
				MatchResult.builder().canUse(true).build());
		userCouponCanUse.setCanUse(result.getCanUse());
		userCouponCanUse.setReason(result.getReason());
		return userCouponCanUse;
	}

	/**
	 * <pre>
	 * 商品过滤
	 * 应用场景：券适用商品列表
	 * </pre>
	 *
	 * @param couponTemplateId
	 * @return 商品编码列表
	 */
	public List<String> filterProduct(Integer couponTemplateId) {
		// 查找有效的优惠券模板
		List<CouponTemplateCondition> couponTemplateConditionList = couponTemplateConditionService
				.selectByCouponTemplateId(couponTemplateId);

		if (CollectionUtils.isEmpty(couponTemplateConditionList)) {
			log.warn("优惠券模板{}使用条件未配置", couponTemplateId);
			return new ArrayList<String>();
		}

		List<List<String>> productCodeListList = couponTemplateConditionList.parallelStream().map(v -> {
			String beanName = v.getUseCondition();
			UseCondition condition = SpringContextUtil.getBean(beanName, UseCondition.class);
			if (condition == null) {
				log.warn("使用条件未配置:{}", beanName);
				return new ArrayList<String>();
			}
			FilterParam filterParam = FilterParam.builder().couponTemplateId(couponTemplateId)
					.useConditionValue(v.getUseConditionValue()).build();
			List<String> productCodeList = condition.filterProduct(filterParam);
			return productCodeList;
		}).filter(v -> v != null).collect(Collectors.toList());

		if (productCodeListList.isEmpty()) {
			return null; // 返回null代表所有商品可用
		}

		return CollectionUtil.intersection(productCodeListList);
	}
}
