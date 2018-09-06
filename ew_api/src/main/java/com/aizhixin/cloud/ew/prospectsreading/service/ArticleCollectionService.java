package com.aizhixin.cloud.ew.prospectsreading.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.ew.article.entity.Article;
import com.aizhixin.cloud.ew.article.repository.ArticleRepository;
import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.prospectsreading.domain.ArticleCollectionDomain;
import com.aizhixin.cloud.ew.prospectsreading.entity.ArticleCollection;
import com.aizhixin.cloud.ew.prospectsreading.repository.ArticleCollectionRepository;

@Component
@Transactional
public class ArticleCollectionService {
	@Autowired
	private ArticleCollectionRepository articleCollectionRepository;
	@Autowired
	private ArticleRepository articleRepository;

	/**
	 * 保存实体
	 * 
	 * @param entity
	 * @return
	 */
	public ArticleCollection save(ArticleCollection entity) {
		return articleCollectionRepository.save(entity);
	}
	
	/**
	 * 根据文章Id查询文章实体
	 * @param articleId
	 * @return
	 */
	public Article findByArticleId(Long articleId) {
		return articleRepository.findOne(articleId);
	}

	/**
	 * 根据用户Id查询所有文章
	 * 
	 * @param page
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ArticleCollectionDomain> findAllPage(Pageable page, Long userId) {
		return articleCollectionRepository.findAll(page, userId, DataValidity.VALID.getState());
	}
	
	/**
	 * 根据文章Id和用户Id查询是否已收藏
	 * 
	 * @param articleId
	 * @return
	 */
	@Transactional(readOnly = true)
	public long countByArticleIdAndUserId(Long articleId, Long userId) {
		return articleCollectionRepository.countByArticle_idAndUserId(articleId, userId);
	}
	
	/**
	 * 根据文章Id和用户Id查询已收藏的文章
	 * 
	 * @param articleId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ArticleCollection> findByArticleIdAndUserId(Long articleId, Long userId) {
		return articleCollectionRepository.findByArticle_idAndUserId(articleId, userId);
	}
	
	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
	
	/**
	 * 保存文章收藏
	 * @param domain
	 * @param userId
	 * @return
	 */
	public RestResult save(Long articleId, Long userId) {
		RestResult r = new RestResult();
		//根据文章Id查询
		Article article = findByArticleId(articleId);
		if (article == null) {
			return new RestResult("error", "文章不存在");
		}
		long c = countByArticleIdAndUserId(articleId, userId);
		if (c > 0) {// 如果通过文章id查询的总条数大于0
			return new RestResult("error", "文章ID[" + articleId + "]已经存在");
		}
		ArticleCollection articleCollection = new ArticleCollection();
		articleCollection.setArticle(article);
		articleCollection.setUserId(userId);
		articleCollection.setCreatedBy(userId);
		articleCollection.setLastModifiedBy(userId);
		articleCollection = save(articleCollection);
		r.setResult("success");
		r.setId(articleCollection.getId());
		return r;
	}
	
	/**
	 * 取消文章收藏
	 * @param articleId
	 * @param userId
	 * @return
	 */
	public RestResult offSave(Long articleId, Long userId) {
		RestResult r = new RestResult();
		List<ArticleCollection> articleCollections = findByArticleIdAndUserId(articleId, userId);
		if (articleId == null || articleId <= 0) {
			return new RestResult("error", "文章id不能为空");
		}
		if (articleCollections == null || articleCollections.size() <= 0) {
			return new RestResult("error", "根据文章id不能查找到对应的数据");
		}
		//根据文章id，查询已收藏的文章
		articleCollectionRepository.delete(articleCollections);
		r.setResult("success");
		return r;
	}
	
	/**
	 * 查询文章收藏列表
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageInfo<ArticleCollectionDomain> list(Pageable pageable, Long userId) {
        PageInfo<ArticleCollectionDomain> r = new PageInfo<>();
        Page<ArticleCollectionDomain> page = findAllPage(pageable, userId);
        r.setData(page.getContent());
        r.setLimit(pageable.getPageSize());
        r.setOffset(pageable.getPageNumber());
        r.setPageCount(page.getTotalPages());
        r.setTotalCount(page.getTotalElements());
        return r;
    }
}
