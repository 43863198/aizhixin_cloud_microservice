package com.aizhixin.cloud.ew.article.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.ArticlePraise;

public interface PraiseRepository extends PagingAndSortingRepository<ArticlePraise, Long> {

	List<ArticlePraise> findAll();

	List<ArticlePraise> findByArticleId(Long articleId);

	List<ArticlePraise> findByArticleIdAndDeleteFlag(Long articleId, int i);

	ArticlePraise findByArticleIdAndUserId(Long articleId, Long id);

}
