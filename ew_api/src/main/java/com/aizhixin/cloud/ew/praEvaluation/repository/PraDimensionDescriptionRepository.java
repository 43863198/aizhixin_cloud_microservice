package com.aizhixin.cloud.ew.praEvaluation.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraDimensionDescription;

public interface PraDimensionDescriptionRepository extends PagingAndSortingRepository<PraDimensionDescription, Long> {

	PraDimensionDescription findByDimensionId(Long dimensionId);

}
