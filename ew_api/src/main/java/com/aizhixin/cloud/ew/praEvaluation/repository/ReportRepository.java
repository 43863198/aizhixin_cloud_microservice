package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.Report;

public interface ReportRepository extends PagingAndSortingRepository<Report, Long> {

	List<Report> findByEvaluationId(Long evaluationId);

	Report findByMaxScore(Double d);

	Report findByMinScoreAndMaxScore(double i, double j);

	Report findByMinScore(Double i);

	Report findByDimensionId(Long dimensionId);

	Report findByCode(String description);

	Report findByChoiceId(Long choiceId);

}
