package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraQuestion;

public interface PraQuestionRepository extends PagingAndSortingRepository<PraQuestion, Long> {

	List<PraQuestion> findByEvaluationId(Long evaluationId);

}
