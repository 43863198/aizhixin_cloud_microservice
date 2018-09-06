package com.aizhixin.cloud.ew.praEvaluation.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.praEvaluation.entity.AppCharacteristic;

public interface AppCharacteristicRepository extends PagingAndSortingRepository<AppCharacteristic, Long> {

	List<AppCharacteristic> findByEvaluationId(Long evaluationId);

	AppCharacteristic findByDimensionAndMaxScore(String s, double d);

	AppCharacteristic findByDimensionAndMinScoreAndMaxScore(String d, double i, double j);

	// AppCharacteristic findByDimensionAndMinScore(String d,double i);

	AppCharacteristic findByDimension(String dimension);

	AppCharacteristic findByCode(String description);

	AppCharacteristic findByDimensionAndMinScore(String dimensionCode, Double score);

}
