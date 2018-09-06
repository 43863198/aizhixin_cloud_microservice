package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.company.domain.YearDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Year;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface YearRepository extends JpaRepository<Year, String> {
	
	Long countByOrgIdAndNameAndDeleteFlag(Long orgId, String name, Integer deleteFlag);

	Long countByOrgIdAndNameAndIdNotAndDeleteFlag(Long orgId, String name, String id, Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.YearDomain(y.id, y.name) from com.aizhixin.cloud.orgmanager.company.entity.Year y where y.deleteFlag = :deleteFlag and y.orgId = :orgId")
	Page<YearDomain> findByPage(Pageable pageable, @Param(value = "deleteFlag") Integer deleteFlag,@Param(value = "orgId") Long orgId);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.YearDomain(y.id, y.name) from com.aizhixin.cloud.orgmanager.company.entity.Year y where y.orgId = :orgId and y.deleteFlag = :deleteFlag and y.name like %:name%")
	Page<YearDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);
	
}
