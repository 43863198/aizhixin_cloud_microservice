package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface CollegeRepository extends JpaRepository<College, Long> {
    Long countByOrgIdAndCodeAndDeleteFlag(Long orgId, String code, Integer deleteFlag);

    Long countByOrgIdAndCodeAndIdNotAndDeleteFlag(Long orgId, String code, Long id, Integer deleteFlag);

    Long countByOrgIdAndNameAndDeleteFlag(Long orgId, String name, Integer deleteFlag);

    Long countByOrgIdAndNameAndIdNotAndDeleteFlag(Long orgId, String name, Long id, Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and c.name like %:name% and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain(c.id, c.code, c.name, c.orgId, c.createdDate) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by c.id desc")
    Page<CollegeDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain(c.id, c.code, c.name, c.orgId, c.createdDate) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag and c.name like %:name% order by c.id desc")
    Page<CollegeDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain(c.id, c.name, c.orgId, c.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.College c where c.orgId = :orgId and c.deleteFlag = :deleteFlag")
//	Page<CollegeDomain> findByOrgIdAndCode(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);
//
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain(c.id, c.name, c.orgId, c.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.College c where c.orgId = :orgId and c.deleteFlag = :deleteFlag and c.name like %:name%")
//	Page<CollegeDomain> findByOrgIdAndNameCode(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

    Long countByOrgIdAndDeleteFlag(Long orgId, Integer deleteFlag);

    List<College> findAllByOrgIdAndDeleteFlag(Long organId, Integer deleteFlag);

    List<College> findByIdIn(Set<Long> ids);

    List<College> findByOrgIdAndCodeInAndDeleteFlag(Long orgId, Set<String> codes, Integer deleteFlag);

    List<College> findByOrgIdAndNameInAndDeleteFlag(Long orgId, Set<String> names, Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain(c.orgId, c.id, c.name) from #{#entityName} c where c.orgId in (:orgIds) and c.deleteFlag = :deleteFlag")
    List<IdIdNameDomain> findByorgIds(@Param(value = "orgIds") Set<Long> orgIds, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.code from #{#entityName} c where c.orgId = :orgId and c.code in (:codes) and c.deleteFlag = :deleteFlag")
    List<String> findCodesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "codes") Set<String> codes, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.name from #{#entityName} c where c.orgId = :orgId and c.name in (:names) and c.deleteFlag = :deleteFlag")
    List<String> findNamesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "names") Set<String> names, @Param(value = "deleteFlag") Integer deleteFlag);
}
