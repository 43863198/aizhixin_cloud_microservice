package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraChoice;

public interface PraChoiceRepository extends PagingAndSortingRepository<PraChoice, Long> {

	List<PraChoice> findByQuestionId(Long questionId);

	List<PraChoice> findByEvaluationId(Long EvaluationId);

	List<PraChoice> findByDimensionId(Long dimensionId);

}
