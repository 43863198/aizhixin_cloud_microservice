package com.aizhixin.cloud.ew.article.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.Classification;

public interface ClassificationsRepository extends PagingAndSortingRepository<Classification, Long> {
	List<Classification> findAll();

}
