package com.aizhixin.cloud.ew.prospectsreading.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.ew.prospectsreading.domain.ArticleCollectionDomain;
import com.aizhixin.cloud.ew.prospectsreading.entity.ArticleCollection;

public interface ArticleCollectionRepository  extends JpaRepository<ArticleCollection, Long> {
	
	@Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.ArticleCollectionDomain(a.article.id, a.article.title, a.article.picUrl, a.article.linkUrl, a.article.hitCount, a.article.praiseCount ) from #{#entityName} a where a.userId = :userId and a.article.deleteFlag = :deleteFlag order by a.createdDate desc")
	Page<ArticleCollectionDomain> findAll(Pageable page, @Param(value = "userId") Long userId, @Param(value = "deleteFlag") Integer deleteFlag);
	
	List<ArticleCollection> findByArticle_idAndUserId(Long articleId, Long userId);

	long countByArticle_idAndUserId(Long articleId, Long userId);
	
}
