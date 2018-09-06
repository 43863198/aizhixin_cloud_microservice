package com.aizhixin.cloud.ew.news.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.ew.news.entity.News;

public interface NewsRepository extends PagingAndSortingRepository<News, Long> {
	List<News> findAll();

	List<News> findByDeleteFlag(Integer deleteFlag);

	Page<News> findAllByDeleteFlag(Integer deleteFlag, Pageable page);

	List<News> findByDeleteFlagAndPublished(int i, int j);

	News findByIdAndDeleteFlag(Long id, Integer deleteFlag);
}
