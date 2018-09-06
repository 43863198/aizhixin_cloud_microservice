package com.aizhixin.cloud.ew.article.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.Article;
import com.aizhixin.cloud.ew.article.entity.ArticleLable;
import com.aizhixin.cloud.ew.article.entity.Lables;

public interface ArticleLablesRepository extends PagingAndSortingRepository<ArticleLable, Long> {
	List<ArticleLable> findAll();

	List<ArticleLable> findByArticleId(Long articleId);

	List<ArticleLable> findByArticleAndDeleteFlag(Article article, int i);

	List<ArticleLable> findByLable(Lables lable);

	List<ArticleLable> findByArticle_IdAndDeleteFlag(Long id, int i);
}
