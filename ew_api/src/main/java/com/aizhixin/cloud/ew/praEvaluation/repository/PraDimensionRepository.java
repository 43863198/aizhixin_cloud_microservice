package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraDimension;

public interface PraDimensionRepository extends PagingAndSortingRepository<PraDimension, Long> {

	List<PraDimension> findByEvaluationId(Long evaluationId);

	// AppDimension findByName(String dimensionName);

}
