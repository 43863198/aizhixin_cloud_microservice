package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraJobsToEvaluation;

public interface PraJobsToEvaluationRepository extends PagingAndSortingRepository<PraJobsToEvaluation, Long> {

	List<PraJobsToEvaluation> findByCode(String code);

	List<PraJobsToEvaluation> findByCodeAndEvaluationId(String code, Long EvaluationId);

}
