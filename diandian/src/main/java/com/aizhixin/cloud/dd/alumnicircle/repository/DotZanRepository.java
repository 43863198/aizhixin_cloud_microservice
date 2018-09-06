package com.aizhixin.cloud.dd.alumnicircle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.alumnicircle.entity.DotZan;

public interface DotZanRepository extends JpaRepository<DotZan, Long>{
	
	public Long countByAlumniCircleIdAndDeleteFlag(Long alumniCircleId,Integer deleteFlag);
	
	public DotZan findByAlumniCircleIdAndUserIdAndDeleteFlag(Long alumniCircleId,Long userId,Integer deleteFlag);
	
	public List<DotZan> findByUserIdAndDeleteFlag(Long userId,Integer deleteFlag);
	
}
