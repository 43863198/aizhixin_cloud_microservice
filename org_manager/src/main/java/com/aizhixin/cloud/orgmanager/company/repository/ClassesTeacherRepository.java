package com.aizhixin.cloud.orgmanager.company.repository;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.ClassTeacherDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.ClassesTeacher;
import com.aizhixin.cloud.orgmanager.company.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ClassesTeacherRepository extends JpaRepository<ClassesTeacher, Long> {

    long countByClassesAndTeacher_idIn(Classes classes, Set<Long> teacherIds);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain(c.teacher.id, c.teacher.name, c.teacher.phone, c.teacher.email, c.teacher.jobNumber, c.teacher.sex) from #{#entityName} c where c.classes = :classes and c.classes.deleteFlag = :deleteFlag")
    List<TeacherDomain> findTeacherByClasses(@Param(value = "classes") Classes classes, @Param(value = "deleteFlag") Integer deleteFlag);

    List<ClassesTeacher> findByClasses(Classes classes);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.classes.id, c.classes.name) from #{#entityName} c where c.teacher = :teacher and c.classes.deleteFlag = :deleteFlag")
    List<IdNameDomain> findClassesIdNameByTeacher(@Param(value = "teacher") User teacher, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.classes from com.aizhixin.cloud.orgmanager.company.entity.ClassesTeacher c where c.teacher = :teacher and c.classes.deleteFlag = :deleteFlag")
    List<Classes> findClassesByTeacher(@Param(value = "teacher") User teacher, @Param(value = "deleteFlag") Integer deleteFlag);

    long countByTeacher(User teacher);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.classes.id, c.teacher.name) from #{#entityName} c where c.classes.id in (:classesIds) and c.classes.deleteFlag = :deleteFlag")
    List<IdNameDomain> findTeacherNameByClassesIds(@Param(value = "classesIds") Set<Long> classesIds, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain(c.teacher.id, c.teacher.name, c.teacher.phone, c.teacher.email, c.teacher.jobNumber, c.teacher.sex) from #{#entityName} c where c.classes.id = :classesId and c.classes.deleteFlag = :deleteFlag")
    List<TeacherDomain> findTeacherByClassesId(@Param(value = "classesId") Long classesId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.teacher.id from #{#entityName} c where c.teacher.college.id = :collegeId and c.teacher.deleteFlag = :deleteFlag")
    List<Long> findClassTeacherIdByCollege(@Param(value = "collegeId") Long collegeId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select c.teacher.id from #{#entityName} c where c.teacher.orgId = :orgId and c.teacher.deleteFlag = :deleteFlag")
    List<Long> findClassTeacherIdByOrg(@Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Modifying
    @Query("delete from #{#entityName} c where c.classes = :classes")
    int deleteByClasses(@Param(value = "classes") Classes classes);
    
    List<ClassesTeacher> findByOrgIdIsNull();
    
    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassTeacherDomain(c.teacher.id,c.teacher.name,c.teacher.sex,c.teacher.jobNumber,c.classes.id,c.classes.name,c.classes.college.name,c.classes.professional.name,c.classes.teachingYear,c.classes.schoolingLength) from #{#entityName} c where c.teacher.name like %:name% or c.teacher.jobNumber like %:jobNumber% and c.orgId = :orgId ")
    Page<ClassTeacherDomain> findTeacherPageByKeywords(Pageable pageable,@Param(value = "name") String name, @Param(value = "jobNumber") String jobNumber,@Param(value = "orgId") Long orgId);
    
    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.ClassTeacherDomain(c.teacher.id,c.teacher.name,c.teacher.sex,c.teacher.jobNumber,c.classes.id,c.classes.name,c.classes.college.name,c.classes.professional.name,c.classes.teachingYear,c.classes.schoolingLength) from #{#entityName} c where c.orgId = :orgId")
    Page<ClassTeacherDomain> findTeacherPage(Pageable pageable,@Param(value = "orgId") Long orgId);
}
