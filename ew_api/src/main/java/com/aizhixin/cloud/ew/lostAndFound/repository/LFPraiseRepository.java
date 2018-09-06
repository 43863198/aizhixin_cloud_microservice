package com.aizhixin.cloud.ew.lostAndFound.repository;


import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.lostAndFound.entity.Praise;


public interface LFPraiseRepository extends PagingAndSortingRepository<Praise, Long>{
	
	Praise findByCreatedByAndLfId(Long createdBy,Long lfId);
	
	List<Praise> findAll();
	
	List<Praise> findByCreatedByAndLfIdIn(Long createdBy, Set<Long> lfIds);
}
