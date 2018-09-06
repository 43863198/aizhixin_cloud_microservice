package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface CourseRepository extends JpaRepository<Course, Long> {

	Long countByOrgIdAndNameAndDeleteFlag(Long orgId, String name, Integer deleteFlag);
	
	Long countByOrgIdAndNameAndIdNotAndDeleteFlag(Long orgId, String name, Long id, Integer deleteFlag);

	Long countByOrgIdAndCodeAndDeleteFlag(Long orgId, String name, Integer deleteFlag);

	Long countByOrgIdAndCodeAndIdNotAndDeleteFlag(Long orgId, String name, Long id, Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.CourseDomain(c.id, c.name, c.code, c.courseDesc, c.createdDate, c.courseProp, c.credit) from com.aizhixin.cloud.orgmanager.company.entity.Course c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by c.createdDate DESC")
	Page<CourseDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.CourseDomain(c.id, c.name, c.code, c.courseDesc, c.createdDate, c.courseProp, c.credit) from com.aizhixin.cloud.orgmanager.company.entity.Course c where c.orgId = :orgId and c.name like %:name% and c.deleteFlag = :deleteFlag order by c.createdDate DESC")
	Page<CourseDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from com.aizhixin.cloud.orgmanager.company.entity.Course c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by c.createdDate DESC")
	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from com.aizhixin.cloud.orgmanager.company.entity.Course c where c.orgId = :orgId and c.name like %:name% and c.deleteFlag = :deleteFlag order by createdDate DESC")
	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase(c.id, c.name, c.code) from com.aizhixin.cloud.orgmanager.company.entity.Course c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by c.createdDate DESC")
	Page<IdCodeNameBase> findIdNameCode(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase(c.id, c.name, c.code) from com.aizhixin.cloud.orgmanager.company.entity.Course c where c.orgId = :orgId and c.name like %:name% and c.deleteFlag = :deleteFlag order by createdDate DESC")
	Page<IdCodeNameBase> findIdNameCode(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

	List<Course> findByIdIn(Set<Long> ids);

	List<Course> findByOrgIdAndCodeIn(Long orgId, Set<String> codes);
	
	List<Course> findByOrgIdAndCodeInAndDeleteFlag(Long orgId, Set<String> codes, Integer deleteFlag);

    @Query("select c.code from #{#entityName} c where c.orgId = :orgId and c.code in (:codes) and c.deleteFlag = :deleteFlag")
    List<String> findCodesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "codes") Set<String> codes, @Param(value = "deleteFlag") Integer deleteFlag);

}
