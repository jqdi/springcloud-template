package com.company.admin.service.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.company.framework.globalresponse.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.admin.entity.base.XSPageModel;
import com.company.admin.entity.security.SecResource;
import com.company.admin.entity.security.SecRole;
import com.company.admin.entity.security.SecStaff;
import com.company.admin.mapper.security.SecRoleDao;
import com.company.admin.springsecurity.UpdateAuthorityFilter;
import com.company.admin.util.XSTreeUtil;

/**
 * 系统角色ServiceImpl
 * Created by JQ棣 on 2017/10/27.
 */
@Service
public class SecRoleService {

    @Autowired
    private SecRoleDao secRoleDao;

    @Autowired
    private SecResourceService secResourceService;

    public void save(SecRole secRole) {
        SecRole criteria = new SecRole();
        criteria.setName(secRole.getName());
        Long count = secRoleDao.count(criteria);
        if (count.compareTo(0L) > 0) {
            ExceptionUtil.throwException("角色已存在");
        }
        secRoleDao.save(secRole);
    }

    @Transactional
    public void remove(SecRole secRole) {
        SecRole existent = get(secRole);
        //校验是否存在员工拥有该角色
        Long staffCount = secRoleDao.countStaff(existent);
        if (staffCount.compareTo(0L) > 0) {
            ExceptionUtil.throwException("角色被使用");
        }
        //校验是否存在组织拥有该角色
        Long organizationCount = secRoleDao.countOrganization(existent);
        if (organizationCount.compareTo(0L) > 0) {
            ExceptionUtil.throwException("角色被使用");
        }
        secRoleDao.removeRoleRes(existent);
        secRoleDao.remove(existent);
        secResourceService.invalidateCache();
    }

    public void update(SecRole secRole) {
        get(secRole);
        if (secRole.getName() != null) {
            SecRole criteria = new SecRole();
            criteria.setName(secRole.getName());
            List<SecRole> existents = secRoleDao.list(criteria);
            if (existents.size() > 0) {
                boolean isSelf = existents.get(0).getId().equals(secRole.getId());
                if (!isSelf) {
                    ExceptionUtil.throwException("角色已存在");
                }
            }
        }
        secRoleDao.update(secRole);
        secResourceService.invalidateCache();
    }

    public SecRole get(SecRole secRole) {
        SecRole existent = secRoleDao.get(secRole);
        if (existent == null) {
            ExceptionUtil.throwException("角色不存在");
        }
        return existent;
    }

    public XSPageModel<?> listAndCount(SecRole secRole) {
        secRole.setDefaultSort("id", "DESC");
        return XSPageModel.build(secRoleDao.list(secRole), secRoleDao.count(secRole));
    }

    public List<SecResource> treeResource(SecRole secRole, SecStaff secStaff) {
        SecRole existent = get(secRole);
        SecResource criteria = new SecResource();
        criteria.setDefaultSort("seq", "DESC");
        List<SecResource> resourceList = secRoleDao.listResourceCombo(criteria);
        List<SecResource> ownedResourceList = secRoleDao.listResource(existent);
        for (Iterator<SecResource> iterator = resourceList.iterator(); iterator.hasNext();) {
            SecResource item = iterator.next();
            //不展示类型为url的资源
            if (item.getType().equals(2)) {
                iterator.remove();
                continue;
            }
            for (SecResource owned : ownedResourceList) {
                if (item.getId().equals(owned.getId())) {
                    item.setChecked(1);
                    break;
                }
            }
        }
        //非超级管理员不能够操作不可分配资源
        if (!secStaff.getType().equals(10)) {
            for (Iterator<SecResource> iterator = resourceList.iterator(); iterator.hasNext();) {
                if (iterator.next().getAssign().equals(0)) {
                    iterator.remove();
                }
            }
        }
        XSTreeUtil.buildTree(resourceList);
        return XSTreeUtil.getSubTrees(resourceList, new SecResource(0L));
    }

    @Transactional
    public void authorizeResource(SecRole secRole, SecStaff secStaff) {
        SecRole existent = get(secRole);
        List<SecResource> resourceList = secRoleDao.listResourceCombo(new SecResource());
        //非超级管理员不能够操作不可分配资源
        if (!secStaff.getType().equals(10)) {
            for (Iterator<SecResource> iterator = secRole.getResourceList().iterator(); iterator.hasNext();) {
                SecResource secResource = iterator.next();
                for (SecResource item : resourceList) {
                    if (secResource.getId().equals(item.getId()) && item.getAssign().equals(0)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        //加入类型为url的资源
        Map<Long, SecResource> map = XSTreeUtil.buildTree(resourceList);
        List<SecResource> urlResource = new ArrayList<>();
        for (Iterator<SecResource> iterator = secRole.getResourceList().iterator(); iterator.hasNext();) {
            SecResource secResource = map.get(iterator.next().getId());
            if (secResource != null && secResource.getChildren() != null) {
                for(SecResource item : secResource.getChildren()) {
                    //非超级管理员不能够操作不可分配资源
                    if (!secStaff.getType().equals(10)) {
                        if (item.getType().equals(2) && item.getAssign().equals(1)) {
                            urlResource.add(item);
                        }
                    }
                    else {
                        if (item.getType().equals(2)) {
                            urlResource.add(item);
                        }
                    }
                }
            }
        }
        secRole.getResourceList().addAll(urlResource);
        secRoleDao.removeRoleRes(existent);
        if (secRole.getResourceList().size() > 0) {
            secRoleDao.saveRoleRes(secRole);
        }
        secResourceService.invalidateCache();
        UpdateAuthorityFilter.lastUpdateAuthorityTime = LocalDateTime.now();
    }
}
