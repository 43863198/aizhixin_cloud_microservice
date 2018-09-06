package com.aizhixin.cloud.dd.alumnicircle.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircle;

public interface AlumniCircleRepository extends PagingAndSortingRepository<AlumniCircle, Long>{
	public AlumniCircle findByIdAndDeleteFlag(Long id,Integer deleteFlag);
	public Page<AlumniCircle> findByOrgIdAndSendToModuleAndDeleteFlagOrderByDzTotalDesc(Pageable page,Long orgId,Integer sendToModule,Integer deleteFlag);
	public Page<AlumniCircle> findBySendToModuleAndDeleteFlagOrderByDzTotalDesc(Pageable page,Integer sendToModule,Integer deleteFlag);
	public Page<AlumniCircle> findByFromUserIdInAndDeleteFlagOrderByCreatedDateDesc(Pageable page,List<Long> fromUserIds,Integer deleteFlag);
}
