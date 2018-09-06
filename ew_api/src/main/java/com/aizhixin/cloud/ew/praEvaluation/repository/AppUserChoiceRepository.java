package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.AppUserChoice;

public interface AppUserChoiceRepository extends PagingAndSortingRepository<AppUserChoice, Long> {

	List<AppUserChoice> findByEvaluationId(Long EvaluationId);

	List<AppUserChoice> findByEvaluationIdAndUserId(long l, Long id);

	AppUserChoice findByEvaluationIdAndUserIdAndTimes(long l, Long userId, int times);

}
