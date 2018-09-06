package com.aizhixin.cloud.ew.article.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.ew.article.domain.ArticleDetailDomain;
import com.aizhixin.cloud.ew.article.domain.ArticleDomain;
import com.aizhixin.cloud.ew.article.domain.LableDomain;
import com.aizhixin.cloud.ew.article.entity.Article;
import com.aizhixin.cloud.ew.article.entity.ArticleLable;
import com.aizhixin.cloud.ew.article.entity.Classification;
import com.aizhixin.cloud.ew.article.entity.Lables;
import com.aizhixin.cloud.ew.article.repository.ArticleLablesRepository;
import com.aizhixin.cloud.ew.article.repository.ArticleRepository;
import com.aizhixin.cloud.ew.article.repository.ClassificationsRepository;
import com.aizhixin.cloud.ew.article.repository.LablesRepository;
import com.aizhixin.cloud.ew.common.PageData;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.core.PublishStatus;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.prospectsreading.repository.ArticleCollectionRepository;

/**
 * 文章管理操作后台API
 * 
 * @author Rigel.ma
 *
 */
@Component
@Transactional
public class ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private ClassificationsRepository classificationRepository;

	@Autowired
	private LablesRepository lablessRepository;

	@Autowired
	private ArticleLablesRepository articleLablesRepository;

	@Autowired
	private ArticleCollectionRepository articleCollectionRepository;

	@Autowired
	private EntityManager em;
	
	/**
	 * 保存实体
	 * 
	 * @param entity
	 * @return
	 */
	public Article save(Article entity) {
		return articleRepository.save(entity);
	}

	/**
	 * 根据ID查询文章
	 */
	public Article findById(Long articleId) {
		return articleRepository.findByIdAndDeleteFlag(articleId, DataValidity.VALID.getState());
	}

	/**
	 * 根据ID查询文章
	 */
	public List<Article> findByDelIdAndpub() {
		return articleRepository.findByDeleteFlagAndPublished(DataValidity.VALID.getState(),
				PublishStatus.PUBLISHED.getState());
	}

	/**
	 * 根据ID查询标签
	 */
	public Lables findOne(Long LableId) {
		return lablessRepository.findOne(LableId);
	}

	/**
	 * 保存实体
	 */
	public ArticleLable save(ArticleLable articleLable) {
		return articleLablesRepository.save(articleLable);
	}

	/**
	 * 根据文章ID查询文章标签表
	 */
	public List<ArticleLable> findByArticleId(Long articleId) {
		return articleLablesRepository.findByArticle_IdAndDeleteFlag(articleId, DataValidity.VALID.getState());
	}

	public Classification findByCId(Long classificationId) {
		return classificationRepository.findOne(classificationId);
	}

	// ***************************************以下部分处理页面调用逻辑***********************************************//

	/**
	 * 新增
	 * 
	 * @param articleDomain
	 * @param account
	 * @return
	 */
	public Map<String, Object> addArticle(ArticleDomain articleDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(articleDomain.getTitle())) {
			result.put("error", "标题不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getContent())) {
			result.put("error", "内容不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getPicUrl())) {
			result.put("error", "图片不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getLinkUrl())) {
			result.put("error", "链接不能为空");
			return result;
		}
		if (null==articleDomain.getClassificationId()||articleDomain.getClassificationId()<=0) {
			result.put("error", "分类Id不能为空");
			return result;
		}
		Article article = new Article();
		article.setTitle(articleDomain.getTitle());
		article.setContent(articleDomain.getContent());
		article.setClassificationId(articleDomain.getClassificationId());
		article.setPicUrl(articleDomain.getPicUrl());
		article.setLinkUrl(articleDomain.getLinkUrl());
		article.setOpenComment(false);
		article.setCommentCount(0L);
		Long random = (long) (1000 + Math.random() * (3000 - 1000 + 1));
		article.setHitCount(random);
		article.setPraiseCount(0L);
		article.setCreatedBy(account.getId());
		article.setCreated(account.getName());
		article.setLastModifiedBy(account.getId());
		article.setPublished(PublishStatus.UNPUBLISHED.getState());
		article.setDeleteFlag(DataValidity.VALID.getState());
		article.setTotal(random);
		article = save(article);
		// 文章标签关系表中保存数据
		if (null != articleDomain.getLableDomains()) {
			for (int i = 0; i < articleDomain.getLableDomains().size(); i++) {
				LableDomain lableDomain = articleDomain.getLableDomains().get(i);
				ArticleLable articleLable = new ArticleLable();
				articleLable.setArticle(article);
				articleLable.setClassificationId(articleDomain.getClassificationId());
				articleLable.setLable(findOne(lableDomain.getLableId()));
				articleLable.setDeleteFlag(DataValidity.VALID.getState());
				save(articleLable);
			}
		}
		result.put("articleId", article.getId());
		return result;
	}

	/**
	 * 修改
	 * 
	 * @param articleDomain
	 * @param account
	 * @return
	 */
	public Map<String, Object> updateArticle(ArticleDomain articleDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == articleDomain.getId() || articleDomain.getId() <= 0) {
			result.put("error", "文章id不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getTitle())) {
			result.put("error", "标题不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getContent())) {
			result.put("error", "内容不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getPicUrl())) {
			result.put("error", "图片不能为空");
			return result;
		}
		if (StringUtils.isEmpty(articleDomain.getLinkUrl())) {
			result.put("error", "链接不能为空");
			return result;
		}
		if (null==articleDomain.getClassificationId()||articleDomain.getClassificationId()<=0) {
			result.put("error", "分类Id不能为空");
			return result;
		}
		Article article = findById(articleDomain.getId());
		if (null == article) {
			result.put("error", "根据id[" + articleDomain.getId() + "]查找不到对应的文章");
			return result;
		}
		article.setTitle(articleDomain.getTitle());
		article.setContent(articleDomain.getContent());
		article.setClassificationId(articleDomain.getClassificationId());
		article.setPicUrl(articleDomain.getPicUrl());
		article.setLinkUrl(articleDomain.getLinkUrl());
		article.setOpenComment(articleDomain.isOpenComment());
		article.setCreatedBy(account.getId());
		article.setLastModifiedBy(account.getId());
		article.setLastModifiedDate(new Date());
		article.setPublished(PublishStatus.UNPUBLISHED.getState());
		save(article);
		// 先删除标签表的数据
		List<ArticleLable> articleLables = findByArticleId(articleDomain.getId());
		if (null != articleLables) {
			for (int i = 0; i < articleLables.size(); i++) {
				ArticleLable articleLable = articleLables.get(i);
				articleLable.setDeleteFlag(DataValidity.INVALID.getState());
				save(articleLable);
			}
		}
		// 文章标签关系表中保存数据
		if (null != articleDomain.getLableDomains()) {
			for (int i = 0; i < articleDomain.getLableDomains().size(); i++) {
				LableDomain lableDomain = articleDomain.getLableDomains().get(i);
				ArticleLable articleLable = new ArticleLable();
				articleLable.setArticle(article);
				articleLable.setClassificationId(articleDomain.getClassificationId());
				articleLable.setLable(findOne(lableDomain.getLableId()));
				articleLable.setDeleteFlag(DataValidity.VALID.getState());
				save(articleLable);
			}
		}
		result.put("articleId", article.getId());
		return result;
	}

	/**
	 * 删除文章
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> deleteArticle(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = findById(articleId);
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		article.setDeleteFlag(DataValidity.INVALID.getState());
		article.setLastModifiedBy(account.getId());
		article.setLastModifiedDate(new Date());
		save(article);
		result.put("result", "success");
		return result;
	}

	/**
	 * 取消发布文章
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> noPublish(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = findById(articleId);
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		if (PublishStatus.PUBLISHED.getState() != article.getPublished()) {
			result.put("error", "该文章还没有发布，不能取消发布");
			return result;
		}
		article.setPublished(PublishStatus.UNPUBLISHED.getState());
		article.setLastModifiedBy(account.getId());
		article.setLastModifiedDate(new Date());
		save(article);
		result.put("result", "success");
		return result;
	}

	/**
	 * 发布文章
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> Publish(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = findById(articleId);
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		if (PublishStatus.UNPUBLISHED.getState() != article.getPublished()) {
			result.put("error", "该文章已经发布，不能重复发布");
			return result;
		}
		article.setPublished(PublishStatus.PUBLISHED.getState());
		article.setLastModifiedBy(account.getId());
		article.setLastModifiedDate(new Date());
		save(article);
		result.put("result", "success");
		return result;
	}

	/**
	 * 文章详情
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> articleDetail(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null != articleId && articleId > 0) {
			Article article = findById(articleId);
			// 根据文章Id和用户Id查询文章是否已收藏
			long c = articleCollectionRepository.countByArticle_idAndUserId(articleId, account.getId());
			if (null != article) {
				article.setHitCount(article.getHitCount() + 1);
				save(article);
				ArticleDetailDomain articleDetailDomain = new ArticleDetailDomain();
				articleDetailDomain.setId(articleId);
				articleDetailDomain.setClassificationId(article.getClassificationId());
				articleDetailDomain.setClassificationName(findByCId(article.getClassificationId()).getName());
				articleDetailDomain.setTitle(article.getTitle());
				articleDetailDomain.setContent(article.getContent());
				articleDetailDomain.setPublishDate(article.getCreatedDate().toString().substring(0, 10));
				articleDetailDomain.setHitCount(article.getHitCount());
				articleDetailDomain.setCommentCount(article.getCommentCount());
				articleDetailDomain.setPraiseCount(article.getPraiseCount());
				articleDetailDomain.setCreatedBy(article.getCreated());
				if (c > 0) {// 如果通过文章id查询的总条数大于0
					articleDetailDomain.setCollectionStatus(1);//已收藏
				} else {
					articleDetailDomain.setCollectionStatus(0);//未收藏
				}
				// 标签应该是一组，应该定义一个域
				List<ArticleLable> articleLables = findByArticleId(articleId);
				List<LableDomain> lableDomains = new ArrayList<>();
				if (null != articleLables) {
					for (int i = 0; i < articleLables.size(); i++) {
						ArticleLable articleLable = articleLables.get(i);
						LableDomain lableDomain = new LableDomain();
						lableDomain.setLableId(articleLable.getLable().getId());
						lableDomain.setLableName(articleLable.getLable().getName());
						lableDomains.add(lableDomain);
					}
				}
				articleDetailDomain.setLableDomains(lableDomains);
				result.put("articleDetailDomain", articleDetailDomain);
			}
		}
		return result;
	}

	/**
	 * 开启评论
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> OpenComment(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = findById(articleId);
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		if (false != article.isOpenComment()) {
			result.put("error", "该文章已经开启评论");
			return result;
		}
		article.setOpenComment(true);
		article.setLastModifiedBy(account.getId());
		article.setLastModifiedDate(new Date());
		save(article);
		result.put("result", "success");
		return result;
	}

	/**
	 * 关闭评论
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> CloseComment(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = findById(articleId);
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		if (true != article.isOpenComment()) {
			result.put("error", "该文章还未开启评论，不能关闭评论");
			return result;
		}
		article.setOpenComment(false);
		article.setLastModifiedBy(account.getId());
		article.setLastModifiedDate(new Date());
		save(article);
		result.put("result", "success");
		return result;
	}

	/**
	 * 批量删除
	 * 
	 * @param articleIds
	 * @param account
	 * @return
	 */
	public Map<String, Object> deleteArticles(Long[] articleIds, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == articleIds || articleIds.length <= 0) {
			result.put("error", "id不能为空");
			return result;
		}
		for (int i = 0; i < articleIds.length; i++) {
			Article article = findById(articleIds[i]);
			if (null == article) {
				result.put("error", "根据id[" + articleIds[i] + "]查找不到对应的文章");
				return result;
			}
			article.setDeleteFlag(DataValidity.INVALID.getState());
			article.setLastModifiedBy(account.getId());
			article.setLastModifiedDate(new Date());
			save(article);
			result.put("result", "success");
		}
		return result;
	}

	/**
	 * 批量发布文章
	 * 
	 * @param articleIds
	 * @param account
	 * @return
	 */
	public Map<String, Object> Publishes(Long[] articleIds, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == articleIds || articleIds.length <= 0) {
			result.put("error", "id不能为空");
			return result;
		}
		for (int i = 0; i < articleIds.length; i++) {
			Article article = findById(articleIds[i]);
			if (null == article) {
				result.put("error", "根据id[" + articleIds[i] + "]查找不到对应的文章");
				return result;
			}
			if (PublishStatus.UNPUBLISHED.getState() != article.getPublished()) {
				result.put("error", "该文章id[" + articleIds[i] + "]已经发布，不能重复发布");
				return result;
			}
			article.setPublished(PublishStatus.PUBLISHED.getState());
			article.setLastModifiedBy(account.getId());
			article.setLastModifiedDate(new Date());
			save(article);
			result.put("result", "success");
		}
		return result;
	}

	/**
	 * App端分页查询文章列表
	 * 
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<ArticleDomain> list(Pageable pageable, Long classificationId, Long labelId) {
		PageData<ArticleDomain> pageData = new PageData<>();
		Map<String, Object> condition = new HashMap<String, Object>();
		// 拼接hql，count和list
		StringBuilder hql = new StringBuilder("SELECT DISTINCT new com.aizhixin.cloud.ew.article.domain.ArticleDomain"
				+ "(ar.id,ar.title,ar.picUrl,ar.linkUrl,ar.classificationId,c.name,ar.hitCount,ar.praiseCount,ar.openComment) "
				+ "FROM com.aizhixin.cloud.ew.article.entity.ArticleLable a,"
				+ "com.aizhixin.cloud.ew.article.entity.Article ar," + "com.aizhixin.cloud.ew.article.entity.Classification c "
				+ "WHERE a.classificationId = c.id AND a.article = ar.id AND a.article.deleteFlag = 0 AND a.article.published = 1 "
				+ "AND a.deleteFlag = 0 ");
		StringBuilder chql = new StringBuilder("SELECT COUNT (DISTINCT ar.id) "
				+ "FROM com.aizhixin.cloud.ew.article.entity.ArticleLable a,"
				+ "com.aizhixin.cloud.ew.article.entity.Article ar," + "com.aizhixin.cloud.ew.article.entity.Classification c "
				+ "WHERE a.classificationId = c.id AND a.article = ar.id AND a.article.deleteFlag = 0 AND a.article.published = 1 "
				+ "AND a.deleteFlag = 0 ");
		if (classificationId != null && classificationId > 0) {
			hql.append("AND ar.classificationId = :classificationId ");
			chql.append("AND ar.classificationId = :classificationId ");
			condition.put("classificationId", classificationId);
		}
		if (labelId != null && labelId > 0) {
			hql.append("AND a.lable.id = :labelId ");
			chql.append("AND a.lable.id = :labelId ");
			condition.put("labelId", labelId);
		}
		hql.append("ORDER BY ar.lastModifiedDate DESC");
		Query q = em.createQuery(chql.toString());
		for (Entry<String, Object> e : condition.entrySet()) {
			q.setParameter(e.getKey(), e.getValue());
		}
		Long count = (Long) q.getSingleResult();
		if (count <= 0) {
			return pageData;
		}
		TypedQuery<ArticleDomain> tq = em.createQuery(hql.toString(), ArticleDomain.class);
		for (Entry<String, Object> e : condition.entrySet()) {
			tq.setParameter(e.getKey(), e.getValue());
		}
		tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		tq.setMaxResults(pageable.getPageSize());
		pageData.setData(tq.getResultList());
		pageData.getPage().setTotalElements(count);
		pageData.getPage().setPageNumber(pageable.getPageNumber());
		pageData.getPage().setPageSize(pageable.getPageSize());
		pageData.getPage().setTotalPages(PageUtil.totalPages(count, pageData.getPage().getPageSize()));
		return pageData;
	}
}
