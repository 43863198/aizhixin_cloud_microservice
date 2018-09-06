package com.aizhixin.cloud.dd.dorms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.dd.dorms.entity.Floor;

public interface FloorRepository extends PagingAndSortingRepository<Floor, Long>{
	public Floor findByIdAndDeleteFlag(Long id,Integer deleteFlag);
	public Floor findByNameAndOrgIdAndDeleteFlagAndIdNot(String name,Long orgId,Integer deleteFlag,Long id);
	public Floor findByNameAndOrgIdAndDeleteFlag(String name,Long orgId,Integer deleteFlag);
	public Page<Floor> findByOrgIdAndDeleteFlagOrderByCreatedDateDesc(Pageable page,Long orgId,Integer deleteFlag);
	public Page<Floor> findByOrgIdAndDeleteFlagAndNameLikeOrderByCreatedDateDesc(Pageable page,Long orgId,Integer deleteFlag,String name);
}
