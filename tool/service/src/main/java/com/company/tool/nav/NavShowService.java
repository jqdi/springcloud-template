package com.company.tool.nav;

import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.tool.api.enums.NavItemEnum;
import com.company.tool.entity.NavItem;
import com.company.tool.entity.NavItemCondition;
import com.company.tool.nav.dto.NavItemCanShow;
import com.company.tool.service.market.NavItemConditionService;
import com.company.tool.service.market.NavItemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NavShowService {
	private static final int POSITION_COVER = 99;// 位置补位值

	@Autowired
	private NavItemService navItemService;
	@Autowired
	private NavItemConditionService navItemConditionService;
	/**
	 * <pre>
	 * 导航栏金刚位列表
	 * </pre>
	 *
	 * @param maxSize
	 *            最多返回多少个
	 * @param runtimeAttach
	 * @return
	 */
	public List<NavItemCanShow> list(int maxSize, Map<String, String> runtimeAttach) {
		LocalDateTime now = LocalDateTime.now();

		// 查询条件符合的金刚位，排序position asc,priority desc,id desc
		List<NavItem> navItemList = navItemService.selectValidOrderby(NavItemEnum.Status.ON, now);
		if (CollectionUtils.isEmpty(navItemList)) {
			return Collections.emptyList();
		}

		Set<Integer> navItemIdSet = navItemList.stream().map(NavItem::getId).collect(Collectors.toSet());
		List<NavItemCondition> navItemConditionBatchList = navItemConditionService.selectByNavItemIds(navItemIdSet);
		Map<Integer, List<NavItemCondition>> navItemIdConditionMap = navItemConditionBatchList.stream()
				.collect(Collectors.groupingBy(NavItemCondition::getNavItemId));

		// 可显示列表
		List<NavItem> navItemListCanShow = Lists.newArrayList();
		for (NavItem navItem : navItemList) {
			List<NavItemCondition> navItemConditionList = navItemIdConditionMap.get(navItem.getId());
			Boolean canShow = this.canShow(navItem, navItemConditionList, runtimeAttach);
			if (!canShow) {// 不可显示的金刚位直接跳过
				continue;
			}
			navItemListCanShow.add(navItem);
		}

		if (CollectionUtils.isEmpty(navItemListCanShow)) {
			return Collections.emptyList();
		}

		// 最终展示列表
		List<NavItem> finalShowList = finalShow(maxSize, navItemListCanShow);

		/* ============定义金刚位配置中的参数============ */
		Map<String, String> configParams = Maps.newHashMap();
		String userId = runtimeAttach.get("userId");
		configParams.put("{userId}", userId);

		String longitude = runtimeAttach.get("longitude");
		configParams.put("{longitude}", longitude);
		String latitude = runtimeAttach.get("latitude");
		configParams.put("{latitude}", latitude);

		String cityCode = runtimeAttach.get("cityCode");
		if (StringUtils.isBlank(cityCode)) {
			cityCode = "440300";
		}
		configParams.put("{cityCode}", cityCode);

		String token = runtimeAttach.get("token");
		configParams.put("{token}", token);

		// 自定义参数替换（可用于复杂参数的生成）
		Map<String, NavReplaceParam> replaceParamBeans = SpringContextUtil.getBeansOfType(NavReplaceParam.class);
		Set<Entry<String, NavReplaceParam>> entrySet = replaceParamBeans.entrySet();
		/* ============定义金刚位配置中的参数============ */

		List<NavItemCanShow> navItemCanShowList = Lists.newArrayList();
		for (NavItem navItem : finalShowList) {
			String attachJson = navItem.getAttachJson();
			if (StringUtils.isBlank(attachJson)) {
				attachJson = "{}";
			}
			@SuppressWarnings("unchecked")
			Map<String, String> attachMap = JsonUtil.toEntity(attachJson, Map.class);

			for (Entry<String, NavReplaceParam> entry : entrySet) {
				String key = entry.getKey();
				boolean anyContains = Utils.anyContains(key, navItem.getTitle(), navItem.getLogo(), navItem.getValue(),
						navItem.getAttachJson());
				if (anyContains) {
					NavReplaceParam replaceParam = entry.getValue();
					Map<String, String> replaceMap = replaceParam.replace(attachMap);
					replaceMap.entrySet().stream().forEach(v -> configParams.put(key + v.getKey(), v.getValue()));
				}
			}

			NavItemCanShow navItemCanShow = new NavItemCanShow();

			navItemCanShow.setTitle(Utils.replaceConfigParams(navItem.getTitle(), configParams));
			navItemCanShow.setLogo(Utils.replaceConfigParams(navItem.getLogo(), configParams));

			navItemCanShow.setType(NavItemEnum.Type.of(navItem.getType()));
			navItemCanShow.setValue(Utils.replaceConfigParams(navItem.getValue(), configParams));
			navItemCanShow.setAttachJson(Utils.replaceConfigParams(navItem.getAttachJson(), configParams));

			navItemCanShowList.add(navItemCanShow);
		}

		return navItemCanShowList;
	}

	/**
	 * 最终展示列表
	 *
	 * @param maxSize
	 * @param navItemList
	 * @return
	 */
	private static List<NavItem> finalShow(int maxSize, List<NavItem> navItemList) {
		List<NavItem> finalShowList = Lists.newArrayList();

		LinkedHashMap<Integer, List<NavItem>> positionNavItemListMap = navItemList.stream()
				.collect(Collectors.groupingBy(NavItem::getPosition, LinkedHashMap::new, Collectors.toList()));

		List<NavItem> positionCoverList = positionNavItemListMap.getOrDefault(POSITION_COVER, Collections.emptyList());
		Queue<NavItem> positionCoverQueue = new LinkedList<>();
		for (NavItem navItem : positionCoverList) {
			positionCoverQueue.offer(navItem);
		}
		positionNavItemListMap.remove(POSITION_COVER);

		int i = 1;
		Set<Entry<Integer, List<NavItem>>> entrySet = positionNavItemListMap.entrySet();
		for (Entry<Integer, List<NavItem>> entry : entrySet) {
			Integer key = entry.getKey();
			for (; i < key; i++) {
				NavItem positionCover = positionCoverQueue.poll();// 使用poll方法，它会移除并返回队列头部的元素，如果没有元素则返回null
				if (positionCover != null) {
					finalShowList.add(positionCover);
					if (finalShowList.size() == maxSize) {
						return finalShowList;
					}
				} else {
					// 没有候补金刚位
				}
			}
			List<NavItem> list = entry.getValue();
			finalShowList.add(list.get(0));// 相同位置的金刚位只保留优先级最高的（也就是第一个）
			if (finalShowList.size() == maxSize) {
				return finalShowList;
			}
			i = key + 1;
		}

		for (; i <= maxSize; i++) {
			NavItem positionCover = positionCoverQueue.poll();// 使用poll方法，它会移除并返回队列头部的元素，如果没有元素则返回null
			if (positionCover != null) {
				finalShowList.add(positionCover);
				if (finalShowList.size() == maxSize) {
					return finalShowList;
				}
			} else {
				// 没有候补金刚位
			}
		}
		return finalShowList;
	}

	private boolean canShow(NavItem navItem, List<NavItemCondition> navItemConditionList,
			Map<String, String> runtimeAttach) {

		if (CollectionUtils.isEmpty(navItemConditionList)) {
			log.warn("金刚位{}展示条件未配置", navItem.getId());
			return false;
		}

		/*
		 * 并发执行条件判断，任意1个匹配false则返回false，否则返回true
		 */
		List<Supplier<Boolean>> supplierList = navItemConditionList.stream().map(v -> {
			Supplier<Boolean> supplier = () -> {
				String beanName = v.getShowCondition();
				NavShowCondition condition = SpringContextUtil.getBean(beanName, NavShowCondition.class);
				if (condition == null) {
					log.warn("展示条件未配置:{}", beanName);
					return false;
				}

				ShowParam showParam = ShowParam.builder().navItemId(navItem.getId()).runtimeAttach(runtimeAttach)
						.showConditionValue(v.getShowConditionValue()).build();

				boolean canShow = false;
				try {
					canShow = condition.canShow(showParam);
				} catch (Exception e) {
					// 异常情况下不显示
					log.error("canShow error", e);
				}
				return canShow;
			};
			return supplier;
		}).collect(Collectors.toList());

		// 任意1个匹配false则返回false，否则返回true
		Boolean result = Utils.anyMatch(supplierList, v -> v == false, true);
		return result;
	}

	public static void main(String[] args) {
		int maxSize = 5;

		List<NavItem> navItemListCanShow = Lists.newArrayList();
		navItemListCanShow.add(new NavItem().setId(1).setPosition(1).setPriority(3));
		navItemListCanShow.add(new NavItem().setId(2).setPosition(1).setPriority(1));
		navItemListCanShow.add(new NavItem().setId(21).setPosition(1).setPriority(0));
		navItemListCanShow.add(new NavItem().setId(3).setPosition(4).setPriority(3));
		navItemListCanShow.add(new NavItem().setId(4).setPosition(4).setPriority(2));

		navItemListCanShow.add(new NavItem().setId(5).setPosition(POSITION_COVER).setPriority(6));
		navItemListCanShow.add(new NavItem().setId(6).setPosition(POSITION_COVER).setPriority(5));
		navItemListCanShow.add(new NavItem().setId(7).setPosition(POSITION_COVER).setPriority(5));
		navItemListCanShow.add(new NavItem().setId(8).setPosition(POSITION_COVER).setPriority(5));

		List<NavItem> finalShowList = finalShow(maxSize, navItemListCanShow);
		System.out.println("finalShowList:" + finalShowList);
	}
}
