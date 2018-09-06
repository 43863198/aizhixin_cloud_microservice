package com.aizhixin.cloud.ew.lostAndFound.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.lostAndFound.entity.Type;

public interface TypeRepository extends PagingAndSortingRepository<Type, Long> {

	List<Type> findAll();

	List<Type> findByIdIn(Set<Long> ids);
}
