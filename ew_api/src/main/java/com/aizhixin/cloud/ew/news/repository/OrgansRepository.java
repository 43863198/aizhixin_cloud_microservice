package com.aizhixin.cloud.ew.news.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.news.entity.Organs;

public interface OrgansRepository extends PagingAndSortingRepository<Organs, Long> {
	List<Organs> findAll();

	List<Organs> findByDeleteFlag(Integer deleteFlag);

	Page<Organs> findAllByDeleteFlag(Integer deleteFlag, Pageable page);

	List<Organs> findByNewsIdAndDeleteFlag(Long newsId, Integer deleteFlag);

}
