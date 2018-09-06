package com.aizhixin.cloud.ew.article.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.ArticleComment;




public interface ArticleCommentRepository extends PagingAndSortingRepository<ArticleComment, Long> {

	List<ArticleComment> findAll();

	List<ArticleComment> findByArticleId(Long articleId);

	List<ArticleComment> findByArticleIdAndDeleteFlag(Long articleId, int i);

	List<ArticleComment> findByArticleIdAndUserId(Long articleId, Long id);

	Page<ArticleComment> findByArticleIdAndDeleteFlag(Pageable pageable,Long articleId, int i);
}
