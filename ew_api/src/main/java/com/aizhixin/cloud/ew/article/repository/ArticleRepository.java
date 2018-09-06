package com.aizhixin.cloud.ew.article.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.Article;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
	List<Article> findAll();

	List<Article> findByDeleteFlag(Integer deleteFlag);

	Page<Article> findAllByDeleteFlag(Integer deleteFlag, Pageable page);

	List<Article> findByDeleteFlagAndPublished(int i, int j);

	Article findById(Long articleId, Integer state);

	Article findByIdAndDeleteFlag(Long articleId, int i);

}
