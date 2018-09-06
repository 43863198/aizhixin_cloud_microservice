package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.dd.rollcall.entity.Revert;

public interface RevertRepository extends PagingAndSortingRepository<Revert, Long>{
	public Long countByAssessIdAndDeleteFlagAndAsses(Long assessId,Integer deleteFlag,boolean asses);
	
	public List<Revert> findByAssessIdInAndDeleteFlagAndAssesOrderByCreatedDateAsc(List<Long> assessId,Integer deleteFlag,boolean asses);
	
//	public List<Revert> findByAssessIdAndDeleteFlagOrderByCreatedDateAsc(Long assessId,Integer deleteFlag);

	public Page<Revert> findByAssessIdAndDeleteFlagAndAssesOrderByCreatedDateAsc(Pageable page,Long assessId,Integer deleteFlag,boolean asses);
	
}
