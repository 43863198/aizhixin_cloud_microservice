package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.company.domain.PeriodDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface PeriodRepository extends JpaRepository<Period, Long> {
	
	Long countByNoAndOrgIdAndDeleteFlag(Integer no, Long orgId,Integer deleteFlag);

	Long countByNoAndOrgIdAndIdNotAndDeleteFlag(Integer no, Long orgId,Long id, Integer deleteFlag);
	
	Long countByOrgIdAndDeleteFlagAndStartTimeBetween(Long orgId, Integer deleteFlag,String startTime,String endTime);
	
	Long countByOrgIdAndDeleteFlagAndEndTimeBetween(Long orgId, Integer deleteFlag,String startTime,String endTime);

	Long countByOrgIdAndIdNotAndDeleteFlagAndStartTimeBetween(Long orgId,Long id, Integer deleteFlag,String startTime,String endTime);

	Long countByOrgIdAndIdNotAndDeleteFlagAndEndTimeBetween(Long orgId,Long id, Integer deleteFlag,String startTime,String endTime);

	Long countByOrgIdAndDeleteFlagAndNoLessThanAndEndTimeAfter(Long orgId,Integer deleteFlag,Integer no,String endTime);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.PeriodDomain(w.id,w.startTime, w.endTime, w.no, w.createdDate,w.orgId) from #{#entityName} w where w.orgId = :orgId and w.deleteFlag = :deleteFlag order by w.no asc")
	Page<PeriodDomain> findByPage(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.PeriodDomain(w.id,w.startTime, w.endTime, w.no, w.createdDate,w.orgId) from #{#entityName} w where w.orgId = :orgId and w.deleteFlag = :deleteFlag and w.no =:no order by w.no asc")
	Page<PeriodDomain> findByPageAndNo(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag,@Param(value = "no") Integer no);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.PeriodDomain(w.id,w.no) from #{#entityName} w where w.orgId = :orgId and w.deleteFlag = :deleteFlag order by w.no asc")
	Page<PeriodDomain> findByMap(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

	List<Period> findByIdIn(Set<Long> ids);

	List<Period> findByOrgIdAndDeleteFlag(Long orgId, Integer deleteFlag);

	List<Period> findByOrgIdAndNoAndDeleteFlag(Long orgId, Integer no, Integer deleteFlag);
}
