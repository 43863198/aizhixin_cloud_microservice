package com.aizhixin.cloud.orgmanager.classschedule.repository;


import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassTeacherInfoDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassTeacher;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface TeachingClassTeacherRepository extends JpaRepository<TeachingClassTeacher, Long> {

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain(t.teacher.id, t.teacher.name, t.teacher.phone, t.teacher.email, t.teacher.jobNumber, t.teacher.sex) from #{#entityName} t where t.teachingClass=:teachingClass order by t.id")
    List<TeacherDomain> findSimpleTeacherByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);

    Long countByTeachingClassAndTeacherIn(TeachingClass teachingClass, List<User> teachers);

    List<TeachingClassTeacher> findByTeachingClassAndTeacher(TeachingClass teachingClass, User teacher);

    Long countByTeachingClass(TeachingClass teachingClass);

    @Query("select t.teacher from #{#entityName} t where t.teachingClass=:teachingClass")
    List<User> findTeacherByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain(t.teachingClass.id, t.teacher.id, t.teacher.name) from #{#entityName} t where t.teachingClass.id in :teachingClassIds order by t.teachingClass, t.id")
    List<IdIdNameDomain> findTeacherByTeachingClassIds(@Param(value = "teachingClassIds") Set<Long> teachingClassIds);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassTeacherInfoDomain(t.teacher.id, t.teacher.name, t.teachingClass.id, t.teacher.college.name) from #{#entityName} t where t.teachingClass.id in :teachingClassIds order by t.teachingClass, t.id")
    List<TeachingClassTeacherInfoDomain> findTeacherInfoByTeachingClassIds(@Param(value = "teachingClassIds") Set<Long> teachingClassIds);

    @Query("select distinct t.teachingClass.id from #{#entityName} t where t.teacher = :teacher and t.semester = :semester")
    Set<Long> findTeachingClassIdByTeacher(@Param(value = "teacher")User teacher, @Param(value = "semester")Semester semester);

    @Query("select distinct t.teachingClass from #{#entityName} t where t.teacher = :teacher and t.semester = :semester")
    Set<TeachingClass> findTeachingClassByTeacher(@Param(value = "teacher")User teacher, @Param(value = "semester")Semester semester);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain(t.teachingClass.id, t.teacher.id, t.teacher.name) from #{#entityName} t where t.teachingClass in(:teachingClasses)")
    List<IdIdNameDomain> findTeacherByTeachingClassesIn(@Param(value = "teachingClasses") List<TeachingClass> teachingClasses);

    List<TeachingClassTeacher> findByTeachingClassIn(List<TeachingClass> teachingClass);

    @Modifying
    @Query("delete from  #{#entityName} t where t.teachingClass in (:teachingClassList)")
    void deleteByTeachingClassIn(@Param(value = "teachingClassList")List<TeachingClass> teachingClassList);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.teachingClass.id, t.teachingClass.name, t.teachingClass.code, t.teachingClass.course.id, t.teachingClass.course.name) from  #{#entityName} t where t.teacher = :teacher and t.semester = :semester")
    List<TeachingClassDomain> findTeachingClassByTeacherAndSemester(@Param(value = "teacher")User teacher, @Param(value = "semester")Semester semester);

    @Query("select distinct t.teachingClass from  #{#entityName} t where t.teacher = :teacher and t.semester = :semester")
    List<TeachingClass> findTeachingClassBySemesterAndTeacher(@Param(value = "semester") Semester semester, @Param(value = "teacher") User teacher);
}