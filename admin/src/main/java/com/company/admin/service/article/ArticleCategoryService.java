package com.company.admin.service.article;

import com.company.admin.entity.article.Article;
import com.company.admin.entity.article.ArticleCategory;
import com.company.admin.mapper.article.ArticleCategoryDao;
import com.company.admin.mapper.article.ArticleDao;
import com.company.admin.util.XSTreeUtil;
import com.company.admin.util.XSUuidUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章分类ServiceImpl
 * Created by JQ棣 on 2017/10/28.
 */
@Service
public class ArticleCategoryService {

    @Autowired
    private ArticleCategoryDao articleCategoryDao;

    @Autowired
    private ArticleDao articleDao;

    public void save(ArticleCategory articleCategory) {
        ArticleCategory existent = new ArticleCategory();
        existent.setKey(articleCategory.getKey());
        Long count = articleCategoryDao.count(existent);
        if (count.compareTo(0L) > 0) {
            ExceptionUtil.throwException("数据已存在");
        }
        //展示及锁定状态必须与父级一致
        if (articleCategory.getParent() != null) {
            ArticleCategory parent = articleCategoryDao.get(articleCategory.getParent());
            if (parent != null) {
                articleCategory.setDisplay(parent.getDisplay());
                articleCategory.setLock(parent.getLock());
            }
        }
        else {
            articleCategory.setLock(0);
        }
        articleCategory.setId(XSUuidUtil.generate());
        articleCategoryDao.save(articleCategory);
    }

    public void remove(ArticleCategory articleCategory) {
        ArticleCategory existent = get(articleCategory);
        if (existent.getLock().equals(1)) {
            ExceptionUtil.throwException("数据已锁定");
        }
        //校验该分类下是否存在子级
        existent = new ArticleCategory();
        existent.setParent(articleCategory);
        Long count = articleCategoryDao.count(existent);
        if (count.compareTo(0L) > 0) {
            ExceptionUtil.throwException("数据已被使用");
        }
        //校验该分类下是否存在图片
        Article article = new Article();
        List<String> cateList = listSubTree(articleCategory.getId());
        article.setCateList(cateList);
        Long count1 = articleDao.count(article);
        if (count1.compareTo(0L) > 0) {
            ExceptionUtil.throwException("数据已被使用");
        }
        articleCategoryDao.remove(articleCategory);
    }

    @Transactional
    public void update(ArticleCategory articleCategory) {
        ArticleCategory existent = get(articleCategory);
        if (articleCategory.getKey() != null) {
            ArticleCategory criteria = new ArticleCategory();
            criteria.setKey(articleCategory.getKey());
            List<ArticleCategory> existents = articleCategoryDao.list(criteria);
            if (existents.size() > 0) {
                boolean isSelf = existents.get(0).getId().equals(existent.getId());
                if (!isSelf) {
                    ExceptionUtil.throwException("数据已存在");
                }
            }
        }
        //不能选择自己或自己的下级作为父级
        List<ArticleCategory> list = articleCategoryDao.listCombo(new ArticleCategory());
        Map<String, ArticleCategory> map = XSTreeUtil.buildTree(list);
        List<ArticleCategory> subList = XSTreeUtil.listSubTree(map.get(articleCategory.getId()));
        if(articleCategory.getParent().getId() != null){
            boolean isSelfSub = subList.stream().anyMatch(s -> s.getId().equals(articleCategory.getParent().getId()));
            if(isSelfSub){
                ExceptionUtil.throwException("不能选择自己或自己的下级作为父级");
            }
        }
        articleCategoryDao.update(articleCategory);
        if (articleCategory.getDisplay() != null) {
            ArticleCategory latest = new ArticleCategory();
            latest.setDisplay(articleCategory.getDisplay());
            //取消展示则所有子级也取消，开启展示则所有父级也开启
            if (articleCategory.getDisplay().equals(0)) {
                List<ArticleCategory> subTreeList = XSTreeUtil.listSubTree(map.get(articleCategory.getId()));
                latest.setList(subTreeList);
            }
            else {
                List<ArticleCategory> treePath = XSTreeUtil.getTreePath(map, map.get(articleCategory.getId()));
                latest.setList(treePath);
                List<ArticleCategory> subTreeList = XSTreeUtil.listSubTree(map.get(articleCategory.getId()));
                latest.getList().addAll(subTreeList);
            }
            articleCategoryDao.batchUpdate(latest);
        }
    }

    public void updateLock(ArticleCategory articleCategory) {
        ArticleCategory existent = get(articleCategory);
        List<ArticleCategory> list = articleCategoryDao.listCombo(new ArticleCategory());
        Map<String, ArticleCategory> map = XSTreeUtil.buildTree(list);
        //解锁则所有子级也解锁，锁定则所有父级也锁定
        ArticleCategory latest = new ArticleCategory();
        if (existent.getLock().equals(0)) {
            latest.setLock(1);
            List<ArticleCategory> treePath = XSTreeUtil.getTreePath(map, map.get(articleCategory.getId()));
            latest.setList(treePath);
        }
        else {
            latest.setLock(0);
            List<ArticleCategory> subTreeList = XSTreeUtil.listSubTree(map.get(articleCategory.getId()));
            latest.setList(subTreeList);
        }
        articleCategoryDao.batchUpdateLock(latest);
    }

    public ArticleCategory get(ArticleCategory articleCategory) {
        ArticleCategory existent = articleCategoryDao.get(articleCategory);
        return existent;
    }

    public List<ArticleCategory> tree(ArticleCategory articleCategory) {
        articleCategory.setDefaultSort("seq", "DESC");
        List<ArticleCategory> list = articleCategoryDao.listCombo(articleCategory);
        XSTreeUtil.buildTree(list);
        return XSTreeUtil.getSubTrees(list, new ArticleCategory(""));
    }

    public List<String> listSubTree(String id) {
        List<ArticleCategory> list = articleCategoryDao.listCombo(new ArticleCategory());
        Map<String, ArticleCategory> map = XSTreeUtil.buildTree(list);
        List<ArticleCategory> cates = XSTreeUtil.listSubTree(map.get(id));
        List<String> orgList = cates.stream().map(a -> a.getId()).collect(Collectors.toList());
        return orgList;
    }

    public ArticleCategory getByKey(String key) {
        ArticleCategory existent = articleCategoryDao.getByKey(key);
        return existent;
    }
}
