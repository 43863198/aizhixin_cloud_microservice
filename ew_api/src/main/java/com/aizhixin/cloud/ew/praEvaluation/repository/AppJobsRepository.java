package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.AppJobs;

public interface AppJobsRepository extends PagingAndSortingRepository<AppJobs, Long> {

	List<AppJobs> findByEvaluationId(Long evaluationId);

	AppJobs findByCode(String description);

}
