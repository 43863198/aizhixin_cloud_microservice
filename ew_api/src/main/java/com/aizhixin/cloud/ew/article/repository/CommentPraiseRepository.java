package com.aizhixin.cloud.ew.article.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.CommentPraise;

public interface CommentPraiseRepository extends PagingAndSortingRepository<CommentPraise, Long> {

	List<CommentPraise> findAll();

	List<CommentPraise> findByCommentId(Long commentId);

	List<CommentPraise> findByCommentIdAndStatus(Long commentId, int i);

	CommentPraise findByCommentIdAndCreatedBy(Long commentId, Long createdBy);

	CommentPraise findByCommentIdAndCreatedByAndStatus(Long commentId, Long id, int i);

	List<CommentPraise> findByCreatedByAndStatusAndCommentIdIn(Long id, int i, Set<Long> acIds);

}
