package com.aizhixin.cloud.ew.article.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.article.entity.Lables;

public interface LablesRepository extends PagingAndSortingRepository<Lables, Long> {

	List<Lables> findAll();

	List<Lables> findByClassificationId(Long classificationId);

	List<Lables> findByClassificationIdIn(Set<Long> cIds);

}
