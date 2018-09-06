package com.aizhixin.cloud.orgmanager.classschedule.repository;


import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassSimpleDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface TeachingClassRepository extends JpaRepository<TeachingClass, Long> {
    Long countByOrgIdAndCode(Long orgId, String code);

    Long countByOrgIdAndCodeAndIdNot(Long orgId, String code, Long id);

//    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.id, t.name, t.code, t.semester.id, t.semester.name, t.course.id, t.course.name, t.classOrStudents, t.teacherNames, t.classesNames, t.studentsCount) from #{#entityName} t where t.orgId=:orgId order by t.id DESC")
//    Page<TeachingClassDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId);
//
//    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.id, t.name, t.code, t.semester.id, t.semester.name, t.course.id, t.course.name, t.classOrStudents, t.teacherNames, t.classesNames, t.studentsCount) from #{#entityName} t where t.orgId=:orgId and t.name like %:name% order by t.id DESC")
//    Page<TeachingClassDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.id, t.name, t.code,t.semester.id, t.semester.name, t.course.id, t.course.name, t.classOrStudents) from #{#entityName} t where t.id in (:ids)")
    List<TeachingClassDomain> findByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(t.id, t.course.name) from #{#entityName} t where t.id in (:ids)")
    List<IdNameDomain> findCouseNameByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain(t.id, t.course.id, t.course.name) from #{#entityName} t where t.id in (:ids)")
    List<IdIdNameDomain> findCouseIdNameByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.company.domain.CourseDomain(t.course.id, t.course.code, t.course.name, t.course.orgId) from #{#entityName} t where t.id in (:ids) ORDER BY t.id DESC")
    List<CourseDomain> findOnlyCouseIdNameByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.company.domain.CourseDomain(t.course.id, t.course.code, t.course.name, t.course.orgId) from #{#entityName} t where t.id in (:ids) ORDER BY t.id DESC")
    Page<CourseDomain> findOnlyCouseIdNameByIds(Pageable pageable, @Param(value = "ids") Set<Long> ids);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(t.id, t.name) from #{#entityName} t where t.id in (:ids) and t.course = :course and t.semester = :semester")
    List<IdNameDomain> findTeachingClassIdNameByIdsAndCourseAndSemester(@Param(value = "ids") Set<Long> ids, @Param(value = "course") Course course, @Param(value = "semester") Semester semester);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassSimpleDomain(t.id, t.name, t.code) from #{#entityName} t where t.id in (:ids)")
    List<TeachingClassSimpleDomain> findIdNameByIds(@Param(value = "ids") Set<Long> ids);

    List<TeachingClass> findByIdIn(Set<Long> ids);

    List<TeachingClass> findByOrgIdAndCodeIn(Long orgId, Set<String> codes);

    @Query("select t.code from #{#entityName} t where t.orgId = :orgId and t.code in (:codes)")
    List<String> findCodeByOrgIdAndCodeIn(@Param(value = "orgId") Long orgId, @Param(value = "codes") Set<String> codes);


    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.id, t.name, t.code, t.semester.id, t.semester.name, t.course.id, t.course.name, t.classOrStudents,t.orgId) from #{#entityName} t where t.id in (:ids)")
    List<TeachingClassDomain> findTeachingClasssAndCourseByIds(@Param(value = "ids") Set<Long> ids);

    long countBySemester (Semester semester);
}