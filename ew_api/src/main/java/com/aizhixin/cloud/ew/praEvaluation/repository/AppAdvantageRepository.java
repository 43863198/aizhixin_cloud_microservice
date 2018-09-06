package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.AppAdvantage;

public interface AppAdvantageRepository extends PagingAndSortingRepository<AppAdvantage, Long> {

	List<AppAdvantage> findByEvaluationId(Long evaluationId);

	AppAdvantage findByCode(String description);

}
