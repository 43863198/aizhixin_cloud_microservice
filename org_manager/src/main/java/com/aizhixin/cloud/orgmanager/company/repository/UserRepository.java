package com.aizhixin.cloud.orgmanager.company.repository;

import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Long countByJobNumberAndOrgIdAndUserTypeAndDeleteFlag(String jobNumber, Long orgId, Integer userType, Integer deleteFlag);

    Long countByJobNumberAndUserTypeAndOrgIdAndIdNotAndDeleteFlag(String jobNumber, Integer userType, Long orgId, Long id, Integer deleteFlag);

    Long countByPhoneAndDeleteFlag(String phone, Integer deleteFlag);

    Long countByPhoneAndIdNotAndDeleteFlag(String phone, Long id, Integer deleteFlag);

    Long countByEmailAndDeleteFlag(String email, Integer deleteFlag);

    Long countByEmailAndIdNotAndDeleteFlag(String email, Long id, Integer deleteFlag);

    User findByAccountIdAndDeleteFlag(Long accountId, Integer deleteFlag);

    User findByIdAndDeleteFlag(Long id,Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.classes = :classes and c.userType = :userType and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findBStudentsIdName(Pageable pageable, @Param(value = "classes") Classes classes, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.classes = :classes and c.userType = :userType and c.deleteFlag = :deleteFlag and c.name like %:name%")
    Page<IdNameDomain> findBStudentsIdName(Pageable pageable, @Param(value = "classes") Classes classes, @Param(value = "userType") Integer userType,
        @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.college = :college and c.userType = :userType and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findBTeachersIdName(Pageable pageable, @Param(value = "college") College college, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.college = :college and c.userType = :userType and c.deleteFlag = :deleteFlag and c.name like %:name%")
    Page<IdNameDomain> findBTeachersIdName(Pageable pageable, @Param(value = "college") College college, @Param(value = "userType") Integer userType,
        @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and c.userType = :userType and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findBTeachersIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and c.userType = :userType and c.deleteFlag = :deleteFlag and c.name like %:name%")
    Page<IdNameDomain> findBTeachersIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "userType") Integer userType, @Param(value = "name") String name,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from #{#entityName} c where c.orgId = :orgId and (c.name like %:querykey% or c.jobNumber like %:querykey%) and c.deleteFlag = :deleteFlag")
    Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "querykey") String querykey,
        @Param(value = "deleteFlag") Integer deleteFlag);

    Long countByClassesAndDeleteFlag(Classes classes, Integer deleteFlag);

    Long countByClassesInAndDeleteFlag(List<Classes> classeses, Integer deleteFlag);

    Long countByProfessionalAndDeleteFlag(Professional professional, Integer deleteFlag);

    Long countByCollegeAndDeleteFlag(College college, Integer deleteFlag);

    List<User> findByIdIn(Set<Long> ids);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain (c.id, c.name, c.jobNumber, c.classes.id, c.classes.name) from #{#entityName} c where c.classes in (:classeses) and c.deleteFlag = :deleteFlag")
    List<TeachStudentDomain> findByClasses(@Param(value = "classeses") List<Classes> classeses, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain (c.id, c.name, c.jobNumber, c.classes.id, c.classes.name,c.professional.id,c.professional.name,c.college.id,c.college.name) from #{#entityName} c join c.professional p join c.college co where c.classes in (:classeses) and c.deleteFlag = :deleteFlag and c.rollcall =:type")
    List<TeachStudentDomain> findByClassesNotIncludeException(@Param(value = "classeses") List<Classes> classeses, @Param(value = "type") Integer type,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain (c.id, c.name, c.jobNumber, c.classes.id, c.classes.name) from #{#entityName} c where c.classes in (:classeses) and c.deleteFlag = :deleteFlag and c.rollcall =:type")
    List<TeachStudentDomain> findByClassesException(@Param(value = "classeses") List<Classes> classeses, @Param(value = "type") Integer type,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.CountDomain(c.classes.id, count(c.id))  from #{#entityName} c where c.classes in (:classeses) and c.deleteFlag = :deleteFlag group by c.classes")
    List<CountDomain> countByClasses(@Param(value = "classeses") List<Classes> classeses, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain (c.id, c.name, c.jobNumber) from #{#entityName} c where c.id in (:ids)")
    List<TeachStudentDomain> findSimpleUserByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.id, c.classes.name, c.professional.id, c.professional.name, c.college.id, c.college.name,c.classes.teachingYear,c.orgId) from #{#entityName} c where c.id in (:ids)")
    List<StudentDomain> findStudentByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.jobNumber, c.sex, c.professional.id, c.professional.name, c.college.id, c.college.name, c.orgId, c.idNumber, c.studentSource) from #{#entityName} c where c.id in (:ids)")
    List<StudentDomain> findStudentNoClassesByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.classes = :classes and c.deleteFlag = :deleteFlag")
    List<StudentDomain> findByClasses(@Param(value = "classes") Classes classes, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.id, c.classes.name) from #{#entityName} c where c.classes in (:classeses) and c.deleteFlag = :deleteFlag")
    Page<StudentDomain> findStudentByClassesIn(Pageable pageable, @Param(value = "classeses") List<Classes> classeses, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.id, c.classes.name,c.professional.id,c.professional.name,c.college.id,c.college.name,c.classes.teachingYear) from #{#entityName} c join c.professional p join c.college co where c.classes in (:classeses) and c.deleteFlag = :deleteFlag and c.rollcall =:type")
    Page<StudentDomain> findStudentByClassesInNotIncludeException(Pageable pageable, @Param(value = "classeses") List<Classes> classeses, @Param(value = "type") Integer type,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.classes in (:classeses) and c.name like %:name% and c.deleteFlag = :deleteFlag")
    List<StudentDomain> findStudentByClassesInAndName(@Param(value = "classeses") List<Classes> classeses, @Param(value = "name") String name,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.id, c.classes.name,c.professional.id,c.professional.name,c.college.id,c.college.name) from #{#entityName} c join c.professional p join c.college co where c.classes.id in (:classesIds) and c.userType = :userType and c.deleteFlag = :deleteFlag")
    List<StudentDomain> findStudentDomainByClassesIds(@Param(value = "classesIds") Set<Long> classesIds, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.id from #{#entityName} c where c.classes.id in (:classesIds) and c.userType = :userType and c.deleteFlag = :deleteFlag")
    List<Long> findStudentIdByClassesIds(@Param(value = "classesIds") Set<Long> classesIds, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.id from #{#entityName} c where c.classes.id = :classesId and c.userType = :userType and c.deleteFlag = :deleteFlag")
    List<Long> findStudentIdByClassesId(@Param(value = "classesId") Long classesId, @Param(value = "userType") Integer userType, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.college = :college and c.userType = :userType and c.deleteFlag = :deleteFlag")
    Page<TeacherDomain> findTeacherByCollege(Pageable pageable, @Param(value = "college") College college, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.college = :college and c.userType = :userType and (c.name like %:name% or c.jobNumber like %:name%) and c.deleteFlag = :deleteFlag")
    Page<TeacherDomain> findTeacherByCollegeAndName(Pageable pageable, @Param(value = "college") College college, @Param(value = "userType") Integer userType,
        @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.classes = :classes and c.userType = :userType and c.deleteFlag = :deleteFlag")
    Page<StudentDomain> findStudentByClasses(Pageable pageable, @Param(value = "classes") Classes classes, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.classes = :classes and c.userType = :userType and (c.name like %:name% or c.jobNumber like %:name%) and c.deleteFlag = :deleteFlag")
    Page<StudentDomain> findStudentByClassesAndName(Pageable pageable, @Param(value = "classes") Classes classes, @Param(value = "userType") Integer userType,
        @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.classes in (:classeses) and c.userType = :userType and c.deleteFlag = :deleteFlag")
    Page<StudentDomain> findStudentByClasses(Pageable pageable, @Param(value = "classeses") List<Classes> classeses, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex) from #{#entityName} c where c.classes in (:classeses) and c.userType = :userType and (c.name like %:name% or c.jobNumber like %:name%) and c.deleteFlag = :deleteFlag")
    Page<StudentDomain> findStudentByClassesAndName(Pageable pageable, @Param(value = "classeses") List<Classes> classeses, @Param(value = "userType") Integer userType,
        @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    List<User> findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(Long orgId, Integer deleteFlag, Integer userType, Set<String> jobNumbers);

    List<User> findByOrgIdAndDeleteFlagAndUserTypeAndIdNumber(Long orgId, Integer deleteFlag, Integer userType, String idNumber);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase(c.id, c.name, c.jobNumber) from #{#entityName} c where c.orgId = :orgId and  c.jobNumber in (:jobNumbers)")
    List<IdCodeNameBase> findSimpleUserByJobNumberIn(@Param(value = "orgId") Long orgId, @Param(value = "jobNumbers") Set<String> jobNumbers);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase(c.id, c.name, c.jobNumber) from #{#entityName} c where c.orgId = :orgId and  c.jobNumber like :jobNumber")
    List<IdCodeNameBase> findSimpleUserByJobNumber(@Param(value = "orgId") Long orgId, @Param(value = "jobNumber") String jobNumber);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase(c.id, c.name, c.jobNumber) from #{#entityName} c where c.id in (:ids)")
    List<IdCodeNameBase> findUserByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select c.jobNumber from #{#entityName} c where c.orgId = :orgId and c.userType = :userType and  c.jobNumber in (:jobNumbers) and c.deleteFlag = :deleteFlag")
    List<String> findJobNumberByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(@Param(value = "orgId") Long orgId, @Param(value = "userType") Integer userType,
        @Param(value = "jobNumbers") Set<String> jobNumbers, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain (c.id, c.name, c.jobNumber, c.phone, c.email, c.sex, c.classes.id, c.classes.name) from #{#entityName} c where c.orgId = :orgId and (c.jobNumber in (:phoneOrJobNumbers) or c.phone in (:phoneOrJobNumbers))  and c.userType = :userType  and c.deleteFlag = :deleteFlag")
    List<StudentSimpleDomain> findSimpleStudentByJobNumberOrPhoneInAndOrgId(@Param(value = "orgId") Long orgId, @Param(value = "phoneOrJobNumbers") Set<String> phoneOrJobNumbers,
        @Param(value = "userType") Integer userType, @Param(value = "deleteFlag") Integer deleteFlag);

    List<User> findByOrgIdAndDeleteFlagAndJobNumberIn(Long orgId, Integer deleteFlag, Set<String> jobNumbers);

    @Query("select id from #{#entityName} c where c.orgId = :orgId ")
    List<Long> findUserIds(@Param(value = "orgId") Long orgId);

    @Query("select id from #{#entityName} c where c.college.id = :collegeId and c.deleteFlag = :deleteFlag ")
    List<Long> findUserIdsByCollegeId(@Param(value = "collegeId") Long collegeId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select id from #{#entityName} c where c.orgId.id = :orgId and c.deleteFlag = :deleteFlag ")
    List<Long> findUserIdsByOrgId(@Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.id, c.classes.name, c.professional.id, c.professional.name, c.college.id, c.college.name) from #{#entityName} c where c.classes in (:classeses) and c.userType = :userType and c.deleteFlag = :deleteFlag")
    List<StudentDomain> findStudentInfoByClasses(@Param(value = "classeses") List<Classes> classeses, @Param(value = "userType") Integer userType,
        @Param(value = "deleteFlag") Integer deleteFlag);

    // @Query("select new com.aizhixin.cloud.orgmanager.company.domain.excel.StudentExportDomain (c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.name, c.classes.code,
    // c.professional.name, c.college.name, c.inSchoolDate) from #{#entityName} c where c.orgId = :orgId and c.userType = :userType and c.deleteFlag = :deleteFlag order by
    // c.createdDate desc")
    // List <StudentExportDomain> findStudentListByOrgId(@Param(value = "orgId") Long orgId, @Param(value = "userType") Integer userType, @Param(value = "deleteFlag") Integer
    // deleteFlag);

    @Modifying
    @Query("update  #{#entityName} t set t.college = :college where t.professional = :professional")
    void updateCollgeByProfesional(@Param(value = "professional") Professional professional, @Param(value = "college") College college);

    @Modifying
    @Query("update  #{#entityName} t set t.professional = :professional where t.classes = :classes")
    void updateProfesionalByClasses(@Param(value = "classes") Classes classes, @Param(value = "professional") Professional professional);

    @Modifying
    @Query("update  #{#entityName} t set t.college = :college where t.classes = :classes")
    void updateCollegeByClasses(@Param(value = "classes") Classes classes, @Param(value = "college") College college);

    Long countByClassesInAndUserTypeAndDeleteFlag(List<Classes> classeses, Integer userType, Integer deleteFlag);

    @Query("select c.classes from #{#entityName} c where c.id = :id")
    Classes findClassesById(@Param(value = "id") Long id);

    List<User> findByOrgIdAndDeleteFlagAndUserType(Long orgId, Integer deleteFlag, Integer userType);

    List<User> findByNameAndIdNumberAndUserTypeAndDeleteFlag(String name, String idNumber, Integer userType, Integer deleteFlag);

    List<User> findByIdNumberInAndUserTypeAndDeleteFlag(Set<String> idNumbers, Integer userType, Integer deleteFlag);

    List<User> findByClassesInAndUserTypeAndDeleteFlag(Set<Classes> classes, Integer userType, Integer deleteFlag);

    long countByProfessionalIdAndDeleteFlagAndUserTypeAndClassesIsNull(Long professionalId, Integer deleteFlag, Integer userType);

    List<User> findByClasses_IdInAndDeleteFlagAndUserType(Set<Long> classesIds,Integer deleteFlag,Integer userType);

    List<User> findByCollege_IdInAndDeleteFlagAndUserType(Set<Long> collegeIds,Integer deleteFlag,Integer userType);

    List<User> findByProfessional_IdInAndDeleteFlagAndUserType(Set<Long> profIds,Integer deleteFlag,Integer userType);
    
    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain (c.id, c.name, c.jobNumber,c.college.id,c.college.name,c.sex) from #{#entityName} c where c.id in (:ids)")
    List<TeachStudentDomain> findTeacherByIds(@Param(value = "ids") Set<Long> ids);
}
