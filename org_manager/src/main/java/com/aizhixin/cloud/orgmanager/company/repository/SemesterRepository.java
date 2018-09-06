package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;



public interface SemesterRepository extends JpaRepository<Semester, Long> {

	Long countByOrgIdAndNameAndDeleteFlag(Long orgId, String name, Integer deleteFlag);

	Long countByOrgIdAndNameAndIdNotAndDeleteFlag(Long orgId, String name, Long id, Integer deleteFlag);
	
	Long countByOrgIdAndDeleteFlagAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long orgId, Integer deleteFlag,Date startDate, Date endDate);
	
//	Long countByOrgIdAndDeleteFlagAndEndDateBetween(Long orgId, Integer deleteFlag,Date startDate,Date endDate);

	Long countByOrgIdAndIdNotAndDeleteFlagAndStartDateBetween(Long orgId,Long id, Integer deleteFlag,Date startDate,Date endDate);

	Long countByOrgIdAndIdNotAndDeleteFlagAndEndDateBetween(Long orgId,Long id, Integer deleteFlag,Date startDate,Date endDate);

	Long countByOrgIdAndCodeAndDeleteFlag(Long orgId, String code, Integer deleteFlag);

	Long countByOrgIdAndCodeAndIdNotAndDeleteFlag(Long orgId, String code, Long id, Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(s.id, s.name) from #{#entityName} s where s.orgId = :orgId and s.deleteFlag = :deleteFlag")
	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain(s.id, s.name, s.startDate, s.endDate, s.numWeek, s.createdDate) from #{#entityName} s where s.orgId = :orgId and s.deleteFlag = :deleteFlag")
	Page<SemesterDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain(s.id, s.name, s.startDate, s.endDate, s.numWeek, s.createdDate) from #{#entityName} s where s.orgId = :orgId and s.deleteFlag = :deleteFlag and s.name like %:name%")
	Page<SemesterDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

//	Long countByYearAndDeleteFlag(Year year, Integer deleteFlag);
//
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain(s.id, s.name, s.startDate, s.endDate, s.numWeek, s.createdDate, s.code) from #{#entityName} s where s.year = :year and s.deleteFlag = :deleteFlag order by endDate asc ")
//	List<SemesterDomain> findByYearAndDeleteFlag(@Param(value = "year") Year year, @Param(value = "deleteFlag") Integer deleteFlag);
//
//	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(s.id, s.name) from #{#entityName} s where s.year = :year and s.deleteFlag = :deleteFlag order by endDate asc ")
//	List<IdNameDomain> findIdNameByYearAndDeleteFlag(@Param(value = "year") Year year, @Param(value = "deleteFlag") Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain(s.id, s.name, s.startDate, s.endDate, s.numWeek, s.createdDate) from #{#entityName} s where s.orgId = :orgId and s.deleteFlag = :deleteFlag and s.startDate <= :startDate and s.endDate >= :endDate ")
	List<SemesterDomain> findByOrgIdAndDate(@Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "startDate") Date startDate,@Param(value = "endDate") Date endDate);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain(s.orgId, s.id, s.name, s.startDate, s.endDate, s.numWeek, s.createdDate) from #{#entityName} s where s.orgId in :orgIds and s.deleteFlag = :deleteFlag and s.startDate <= :startDate and s.endDate >= :endDate ")
	List<SemesterDomain> findByOrgIdsAndDate(@Param(value = "orgIds") List<Long> orgIds, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "startDate") Date startDate,@Param(value = "endDate") Date endDate);

	List<Semester> findByOrgIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeleteFlag(Long orgId, Date startDate, Date endDate, Integer deleteFlag);

	List<Semester> findByIdIn(Set<Long> ids);

	List<Semester> findByCodeIn(Set<String> codes);

	List<Semester> findByOrgIdAndCodeAndDeleteFlag(Long orgId, String code, Integer deleteFlag);

	List<Semester> findByOrgIdAndCodeInAndDeleteFlag(Long orgId, Set<String> codes, Integer deleteFlag);
}
