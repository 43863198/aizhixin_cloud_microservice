package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.ClassesIdNameCollegeNameDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface ClassesRepository extends JpaRepository<Classes, Long> {
    Long countByProfessionalAndNameAndDeleteFlag(Professional professional, String name, Integer deleteFlag);

    Long countByProfessionalAndNameAndIdNotAndDeleteFlag(Professional professional, String name, Long id, Integer deleteFlag);

    Long countByOrgIdAndCodeAndDeleteFlag(Long orgId, String code, Integer deleteFlag);

    Long countByOrgIdAndCodeAndIdNotAndDeleteFlag(Long orgId, String code, Long id, Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.professional = :professional and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "professional") Professional professional, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.professional = :professional and c.name like %:name% and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "professional") Professional professional, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.college = :college and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.college = :college and c.name like %:name% and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "college") College college, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and c.name like %:name% and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate) from #{#entityName} c where c.professional = :professional and c.deleteFlag = :deleteFlag order by c.id desc")
//	Page<ClassesDomain> findByProfessional(Pageable pageable, @Param(value = "professional") Professional professional, @Param(value = "deleteFlag") Integer deleteFlag);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by c.id desc")
//	Page<ClassesDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate) from #{#entityName} c where c.professional = :professional and c.deleteFlag = :deleteFlag and c.name like %:name% order by c.id desc")
//	Page<ClassesDomain> findByProfessionalAndName(Pageable pageable, @Param(value = "professional") Professional professional, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.Classes c where c.orgId = :orgId and c.deleteFlag = :deleteFlag and c.name like %:name%")
//	Page<ClassesDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate) from #{#entityName} c where c.college = :college and c.deleteFlag = :deleteFlag")
//	Page<ClassesDomain> findByColleage(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag);
//
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate) from #{#entityName} c where c.college = :college and c.deleteFlag = :deleteFlag and c.name like %:name%")
//	Page<ClassesDomain> findByColleageAndName(Pageable pageable, @Param(value = "college") College college, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag and c.name like %:name% order by c.id desc")
//	Page<ClassesDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

    Long countByProfessionalAndDeleteFlag(Professional professional, Integer deleteFlag);

    List<Classes> findByIdIn(Set<Long> ids);

    List<Classes> findByCodeIn(Set<String> codes);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesIdNameCollegeNameDomain(c.id, c.name, c.professional.id, c.professional.name, c.college.id, c.college.name) from #{#entityName} c where c.id in (:ids)")
    List<ClassesIdNameCollegeNameDomain> findNameAndCollegeNameByIdIn(@Param(value = "ids") Set<Long> ids);

    @Query("select c.code from #{#entityName} c where c.orgId = :orgId and c.code in (:codes) and c.deleteFlag = :deleteFlag")
    List<String> findCodesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "codes") Set<String> codes, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.name from #{#entityName} c where c.orgId = :orgId and c.name in (:names) and c.deleteFlag = :deleteFlag")
    List<String> findNamesByOrgIdAndCodeInAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "names") Set<String> names, @Param(value = "deleteFlag") Integer deleteFlag);

    List<Classes> findByOrgIdAndCodeInAndDeleteFlag(Long orgId, Set<String> codes, Integer deleteFlag);

    List<Classes> findByOrgIdAndNameInAndDeleteFlag(Long orgId, Set<String> names, Integer deleteFlag);

    @Modifying
    @Query("update  #{#entityName} t set t.college = :college where t.professional = :professional")
    void updateNewCollegeByProfesional(@Param(value = "professional") Professional professional, @Param(value = "college") College college);

    List<Classes> findByOrgIdAndDeleteFlag(Long orgId, Integer deleteFlag);
}