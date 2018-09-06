package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
	Long countByCollegeAndCodeAndDeleteFlag(College college, String code, Integer deleteFlag);

	Long countByCollegeAndCodeAndIdNotAndDeleteFlag(College college, String code, Long id, Integer deleteFlag);

	Long countByCollegeAndNameAndDeleteFlag(College college, String name, Integer deleteFlag);

	Long countByCollegeAndNameAndIdNotAndDeleteFlag(College college, String name, Long id, Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.college = :college and c.deleteFlag = :deleteFlag")
	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.college = :college and c.name like %:name% and c.deleteFlag = :deleteFlag")
	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "college") College college, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);
	
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.code, c.name, c.college.id, c.college.name, c.createdDate) from #{#entityName} c where c.college = :college and c.deleteFlag = :deleteFlag order by c.id desc")
//	Page<ProfessionnalDomain> findByCollege(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.code, c.name, c.college.id, c.college.name, c.createdDate) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by c.id desc")
//	Page<ProfessionnalDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);
	
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.code, c.name, c.college.id, c.college.name, c.createdDate) from #{#entityName} c where c.college = :college and c.deleteFlag = :deleteFlag and c.name like %:name% order by c.id desc")
//	Page<ProfessionnalDomain> findByCollegeAndName(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.code, c.name, c.college.id, c.college.name, c.createdDate) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag and c.name like %:name% order by c.id desc")
//	Page<ProfessionnalDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);
	
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.name, c.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.Professional c where c.college = :college and c.deleteFlag = :deleteFlag and c.code = :code")
//	Page<ProfessionnalDomain> findByCollegeAndCode(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "code") String code);
//
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.name, c.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.Professional c where c.college = :college and c.deleteFlag = :deleteFlag and c.name like %:name% and c.code = :code")
//	Page<ProfessionnalDomain> findByCollegeAndNameCode(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name, @Param(value = "code") String code);

	Long countByCollegeAndDeleteFlag(College college, Integer deleteFlag);

	List<Professional> findByIdIn(Set<Long> ids);

	List<Professional> findByCodeIn(Set<String> codes);

	List<Professional> findByOrgIdAndCodeInAndDeleteFlag(Long orgId, Set<String> codes, Integer deleteFlag);

	List<Professional> findByOrgIdAndNameInAndDeleteFlag(Long orgId, Set<String> names, Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.name, c.college.id, c.college.name) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag and  c.college.deleteFlag = :deleteFlag and (c.name like %:name%  or c.college.name like %:name%)")
	List<ProfessionnalDomain> findProfessionalAndCollege(@Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain(c.college.id, c.id, c.name) from #{#entityName} c where c.college.id in (:collegeIds) and c.deleteFlag = :deleteFlag")
	List<IdIdNameDomain> findIdNameByCollegeIdIn(@Param(value = "collegeIds") Set<Long> collegeIds, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select c.code from #{#entityName} c where c.orgId = :orgId and c.code in (:codes) and c.deleteFlag = :deleteFlag")
	List<String> findCodesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "codes") Set<String> codes, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select c.name from #{#entityName} c where c.orgId = :orgId and c.name in (:names) and c.deleteFlag = :deleteFlag")
	List<String> findNamesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "names") Set<String> names, @Param(value = "deleteFlag") Integer deleteFlag);

	List<Professional> findByOrgIdAndDeleteFlag(Long orgId, Integer deleteFlag);
}
