package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.PraAnswerRecord;

public interface PraAnswerRecordRepository extends PagingAndSortingRepository<PraAnswerRecord, Long> {

	List<PraAnswerRecord> findByEvaluationIdAndRecordDate(Long evaluationId, Date date);

	List<PraAnswerRecord> findByEvaluationIdAndDimensionIdAndRecordDate(Long evaluationId, Long id, Date date);

	List<PraAnswerRecord> findByUserIdAndEvaluationIdAndDimensionIdAndRecordDate(Long userId, Long evaluationId,
			Long dimensionId, String date);

	List<PraAnswerRecord> findByUserIdAndEvaluationIdAndDimensionId(Long userId, Long evaluationId, Long id);

	List<PraAnswerRecord> findByUserIdAndEvaluationId(Long userId, long evaluationId);

	List<PraAnswerRecord> findByUserIdAndEvaluationIdAndRecordDate(Long userId, long l, String reportDate);

	List<PraAnswerRecord> findByUserIdAndEvaluationIdAndTimes(Long userId, long l, int times);

	List<PraAnswerRecord> findByUserIdAndEvaluationIdAndDimensionIdAndTimes(Long userId, long l, Long dimensionId,
			int times);

}
