package com.aizhixin.cloud.orgmanager.electrict.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceInfo;
import org.springframework.data.repository.query.Param;


public interface ElectricFenceInfoRepository extends PagingAndSortingRepository<ElectricFenceInfo, Long>,
JpaSpecificationExecutor<ElectricFenceInfo> {
	
	ElectricFenceInfo findOneByOrganIdAndDeleteFlag(Long organId, Integer deleteFlag);

    @Query(value = "select e.id from #{#entityName} e where e.organId = :organId and e.setupOrClose =:setupOrClose and e.deleteFlag = :deleteFlag")
    Long findIdByOranIdAndSetupOrClose(@Param(value = "organId") Long organId, @Param(value = "setupOrClose") Integer setupOrClose, @Param(value = "deleteFlag") Integer deleteFlag);
	
    @Modifying
    @Query(value = "update #{#entityName} e set e.lltudes = ?2,e.monitorDate = ?3, e.nomonitorDate = ?4, e.semesterId = ?5 where e.organId = ?1 ")
    void updateElectricFenceInfo(Long organId, String lltudes,String monitorDate,String nomonitorDate, Long semesterId);
}
