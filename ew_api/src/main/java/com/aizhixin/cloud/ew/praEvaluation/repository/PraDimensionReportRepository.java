package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraDimensionReport;

public interface PraDimensionReportRepository extends PagingAndSortingRepository<PraDimensionReport, Long> {

	List<PraDimensionReport> findByUserIdAndDimensionId(Long userId, Long dimensionId);

	PraDimensionReport findByDimensionId(Long dimensionId);

	List<PraDimensionReport> findByUserIdAndEvaluationIdAndTimes(Long id, Long evaluationId, int i);

	List<PraDimensionReport> findByUserIdAndEvaluationId(Long id, Long evaluationId);

	List<PraDimensionReport> findByEvaluationIdAndUserIdAndTimes(long l, Long userId, Integer times);

	PraDimensionReport findByUserIdAndEvaluationIdAndNameAndTimes(Long userId, long l, String string, int times);

}
