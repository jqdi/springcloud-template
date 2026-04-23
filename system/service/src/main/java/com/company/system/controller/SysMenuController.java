package com.company.system.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.framework.util.PropertyUtils;
import com.company.system.api.constant.Constants;
import com.company.system.api.feign.SysMenuFeign;
import com.company.system.api.request.RemoveReq;
import com.company.system.api.request.SysMenuReq;
import com.company.system.api.response.PageResp;
import com.company.system.api.response.RouterResp;
import com.company.system.api.response.SysMenuResp;
import com.company.system.dto.MenuTreeNode;
import com.company.system.entity.SysMenu;
import com.company.system.enums.SysMenuEnum;
import com.company.system.service.SysMenuService;
import com.company.system.service.SysRoleService;

@RestController
@RequestMapping("/sysMenu")
public class SysMenuController implements SysMenuFeign {

	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysRoleService sysRoleService;

	private QueryWrapper<SysMenu> toQueryWrapper(Integer parentId, String menuName, String menuType, String status,
												 Integer visible, String perms, String createTimeStart, String createTimeEnd) {
		QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
		if (parentId != null) {
			queryWrapper.eq("parent_id", parentId);
		}
		if (StringUtils.isNotBlank(menuName)) {
			queryWrapper.like("menu_name", menuName);
		}
        if (StringUtils.isNotBlank(menuType)) {
            queryWrapper.eq("menu_type", menuType);
        }
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
		if (visible != null) {
			queryWrapper.eq("visible", visible);
		}
		if (StringUtils.isNotBlank(perms)) {
			queryWrapper.like("perms", perms);
		}
        if (StringUtils.isNotBlank(createTimeStart)) {
            queryWrapper.ge("create_time", createTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd + " 23:59:59");
        }
		return queryWrapper;
	}

	@Override
	public PageResp<SysMenuResp> page(Long current, Long size, Integer parentId, String menuName,
											  String menuType, String status, Integer visible, String perms,
											  String createTimeStart, String createTimeEnd) {
		QueryWrapper<SysMenu> queryWrapper = toQueryWrapper(parentId, menuName, menuType, status, visible, perms,
				createTimeStart, createTimeEnd);

		long count = sysMenuService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysMenu> list = sysMenuService.list(PageDTO.of(current, size), queryWrapper);

		List<SysMenuResp> respList = PropertyUtils.copyArrayProperties(list, SysMenuResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysMenuResp> list(Integer parentId, String menuName, Integer orderNum, String menuType,
										  String status, Integer visible, String perms,
										  String createTimeStart, String createTimeEnd) {
		QueryWrapper<SysMenu> queryWrapper = toQueryWrapper(parentId, menuName, menuType, status, visible, perms,
				createTimeStart, createTimeEnd);

		queryWrapper.orderByDesc("id");
		List<SysMenu> list = sysMenuService.list(queryWrapper);

		List<SysMenuResp> respList = PropertyUtils.copyArrayProperties(list, SysMenuResp.class);
		return respList;
	}

	@Override
	public SysMenuResp query(Integer id) {
		SysMenu sysMenu = sysMenuService.getById(id);
		SysMenuResp sysMenuResp = PropertyUtils.copyProperties(sysMenu, SysMenuResp.class);
		return sysMenuResp;
	}

	@Override
	public Boolean save(SysMenuReq sysMenuReq) {
		SysMenu sysMenu = PropertyUtils.copyProperties(sysMenuReq, SysMenu.class);
		boolean success = sysMenuService.save(sysMenu);
		return success;
	}

	@Override
	public Boolean update(SysMenuReq sysMenuReq) {
		SysMenu sysMenu = PropertyUtils.copyProperties(sysMenuReq, SysMenu.class);
		boolean success = sysMenuService.updateById(sysMenu);
		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysMenuService.removeBatchByIds(req.getIdList());
		return success;
	}

	@Override
	public List<RouterResp> getRouters(Integer userId) {
		boolean isAdmin = sysRoleService.hasRole(userId, Constants.SUPER_ROLE);
		List<MenuTreeNode> nodeList = sysMenuService.getTree(userId, isAdmin);
		return buildRouters(nodeList);
	}

	private List<RouterResp> buildRouters(List<MenuTreeNode> nodeList) {
		List<RouterResp> routers = new ArrayList<>();

		for (MenuTreeNode node : nodeList) {
			RouterResp router = new RouterResp();
			router.setHidden(SysMenuEnum.Visible.HIDE.getCode().equals(node.getVisible()));
			router.setName(StringUtils.capitalize(node.getPath()));
			// 获取路由地址
			String routerPath = node.getPath();
			if (node.getParentId() == 0 && SysMenuEnum.Type.DIR.getCode().equals(node.getMenuType())) {
				routerPath = "/" + routerPath;
			}
			router.setPath(routerPath);
			router.setRedirect(StringUtils.isNotBlank(node.getRedirect()) ? node.getRedirect() : null);
			// 获取组件信息
			String component = SysMenuEnum.Component.LAYOUT.getCode();
			if (StringUtils.isNotBlank(node.getComponent())) {
				component = node.getComponent();
			}
			router.setComponent(component);
			RouterResp.Meta meta = new RouterResp.Meta();
			meta.setTitle(node.getMenuName());
			meta.setIcon(node.getIcon());
			router.setMeta(meta);
			List<MenuTreeNode> cMenus = node.getChildren();
			if (!CollectionUtils.isEmpty(cMenus) && SysMenuEnum.Type.DIR.getCode().equals(node.getMenuType())) {
				router.setChildren(buildRouters(cMenus));
			}
			routers.add(router);
		}

		return routers;
	}
}
