package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraUserReport;

public interface PraUserReportRepository extends PagingAndSortingRepository<PraUserReport, Long> {

	List<PraUserReport> findByUserId(Long userId);

	List<PraUserReport> findByUserIdAndEvaluationId(Long id, Long evaluationId);

	List<PraUserReport> findByEvaluationId(Long evaluationId);

	PraUserReport findByUserIdAndEvaluationIdAndTimes(Long id, long l, Integer times);

}
