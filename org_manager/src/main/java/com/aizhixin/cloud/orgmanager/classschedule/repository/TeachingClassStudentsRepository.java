package com.aizhixin.cloud.orgmanager.classschedule.repository;


import com.aizhixin.cloud.orgmanager.classschedule.domain.ClassesCollegeDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassAndClasses;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface TeachingClassStudentsRepository extends JpaRepository <TeachingClassStudents, Long> {

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(t.student.id, t.student.name, t.student.phone, t.student.email, t.student.jobNumber, t.student.sex, t.student.classes.id, t.student.classes.name) from  #{#entityName} t where t.teachingClass=:teachingClass")
    Page <StudentDomain> findSimpleStudentByTeachingClass(Pageable pageable, @Param(value = "teachingClass") TeachingClass teachingClass);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(t.student.id, t.student.name, t.student.phone, t.student.email, t.student.jobNumber, t.student.sex, t.student.classes.id, t.student.classes.name) from  #{#entityName} t where t.teachingClass=:teachingClass and (t.student.name like %:name% or t.student.jobNumber like %:name%)")
    Page <StudentDomain> findSimpleStudentByTeachingClassAndName(Pageable pageable, @Param(value = "teachingClass") TeachingClass teachingClass, @Param(value = "name") String name);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(t.student.id, t.student.name, t.student.phone, t.student.email, t.student.jobNumber, t.student.sex, t.student.classes.id, t.student.classes.name,t.student.professional.id,t.student.professional.name,t.student.college.id,t.student.college.name,t.student.classes.teachingYear) from  #{#entityName} t join t.student s join t.student.classes sl join t.student.professional p join t.student.college c where t.teachingClass=:teachingClass and s.rollcall =:type")
    Page <StudentDomain> findSimpleStudentByTeachingClassNotIncludeException(Pageable pageable, @Param(value = "teachingClass") TeachingClass teachingClass, @Param(value = "type") Integer type);

    Long countByTeachingClassAndStudentIn(TeachingClass teachingClass, List <User> students);

    List <TeachingClassStudents> findByTeachingClassAndStudent(TeachingClass teachingClass, User student);

    List <TeachingClassStudents> findByTeachingClassAndStudentIn(TeachingClass teachingClass, List <User> students);

    Long countByTeachingClass(TeachingClass teachingClass);

    Long countByTeachingClass_id(Long teachingClassId);

    @Query("select distinct t.teachingClass.id from  #{#entityName} t where t.student = :student and t.semester = :semester")
    Set <Long> findTeachingClassByStudent(@Param(value = "student") User student, @Param(value = "semester") Semester semester);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(c.id, c.name) from  #{#entityName} t join t.student.classes c where t.teachingClass=:teachingClass")
    List <IdNameDomain> findSimpleClassByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.ClassesCollegeDomain(t.teachingClass.id, c.id, c.name,g.id,g.name) from  #{#entityName} t join t.student.classes c join t.student.college g where t.teachingClass.id in (:teachingClassIds)")
    List <ClassesCollegeDomain> findSimpleClassByTeachingClassIds(@Param(value = "teachingClassIds") Set teachingClassIds);


    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain(t.student.id, t.student.name,t.student.jobNumber,t.student.classes.id,t.student.classes.name, t.teachingClass.id) from  #{#entityName} t where t.teachingClass in (:teachingClasses)")
    List <TeachStudentDomain> findTeachStudentByTeachingClass(@Param(value = "teachingClasses") List <TeachingClass> teachingClasses);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain(t.student.id, t.student.name,t.student.jobNumber) from  #{#entityName} t where t.teachingClass = :teachingClass")
    List <TeachStudentDomain> findTeachStudentByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.student.id, c.student.name, c.student.phone, c.student.email, c.student.jobNumber, c.student.sex) from  #{#entityName} c where c.teachingClass  in (:teachingClasses) and c.student.name like %:name% and c.student.classes not in (:classeses)")
    List <StudentDomain> findTeachStudentByTeachingClassInAndClassesNotIn(@Param(value = "teachingClasses") List <TeachingClass> teachingClasses, @Param(value = "classeses") List <Classes> classeses, @Param(value = "name") String name);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.student.id, c.student.name, c.student.phone, c.student.email, c.student.jobNumber, c.student.sex) from  #{#entityName} c where c.teachingClass  in (:teachingClasses) and c.student.name like %:name%")
    List <StudentDomain> findTeachStudentByTeachingClassIn(@Param(value = "teachingClasses") List <TeachingClass> teachingClasses, @Param(value = "name") String name);

    List <TeachingClassStudents> findByTeachingClass(TeachingClass teachingClass);

    @Query("select distinct c.teachingClass from  #{#entityName} c where c.student=:student and c.teachingClass.course in (:courses) and c.teachingClass.semester=:semester")
    List <TeachingClass> findByTeachingClassByStudentAndCourses(@Param(value = "student") User student, @Param(value = "courses") List <Course> courses, @Param(value = "semester") Semester semester);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.student.id, c.student.name, c.student.phone, c.student.email, c.student.jobNumber, c.student.sex) from  #{#entityName} c where c.teachingClass  = :teachingClasses and (c.student.name like %:name% or c.student.jobNumber like %:name%) and c.student.deleteFlag = :deleteFlag")
    Page <StudentDomain> findPageTeachStudentByTeachingClassesAndName(Pageable pageable, @Param(value = "teachingClasses") TeachingClass teachingClasses, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.student.id, c.student.name, c.student.phone, c.student.email, c.student.jobNumber, c.student.sex) from  #{#entityName} c where c.teachingClass  = :teachingClasses and c.student.deleteFlag = :deleteFlag")
    Page <StudentDomain> findPageTeachStudentByTeachingClasses(Pageable pageable, @Param(value = "teachingClasses") TeachingClass teachingClasses, @Param(value = "deleteFlag") Integer deleteFlag);

    List <TeachingClassStudents> findByIdIn(Set <Long> ids);

    @Modifying
    @Query("delete from  #{#entityName} t where t.teachingClass in (:teachingClassList)")
    void deleteByTeachingClassIn(@Param(value = "teachingClassList") List <TeachingClass> teachingClassList);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(c.teachingClass.id, c.teachingClass.name, c.teachingClass.code, c.teachingClass.course.id, c.teachingClass.course.name) from  #{#entityName} c where c.student=:student and c.teachingClass.semester=:semester")
    List <TeachingClassDomain> findTeachingClassByStudentAndSemester(@Param(value = "student") User student, @Param(value = "semester") Semester semester);


    @Query("select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain(c.student.id, c.student.name, c.student.phone, c.student.email, c.student.jobNumber, c.student.sex, c.student.classes.id, c.student.classes.name, c.student.professional.id, c.student.professional.name, c.student.college.id, c.student.college.name) from  #{#entityName} c where c.teachingClass  = :teachingClasses and c.student.userType = :userType and c.student.deleteFlag = :deleteFlag")
    List <StudentDomain> findPageTeachStudentInfoByTeachingClasses(@Param(value = "teachingClasses") TeachingClass teachingClasses, @Param(value = "userType") Integer userType, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select distinct c.teachingClass from  #{#entityName} c where c.teachingClass.orgId = :orgId and c.teachingClass.semester=:semester")
    List <TeachingClass> findAllTeachingClass(@Param(value = "orgId") Long orgId, @Param(value = "semester") Semester semester);

    @Modifying
    @Query("delete from  #{#entityName} t where t.student = :student")
    void deleteByStudent(@Param(value = "student") User student);

    @Modifying
    @Query("delete from  #{#entityName} t where t.student = :student and t.semester = :semester")
    void deleteByStudentAndSemester(@Param(value = "student") User student, @Param(value = "semester") Semester semester);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.CountDomain(c.teachingClass.id, count(c.student))  from  #{#entityName} c where c.teachingClass in (:teachingClassList) group by c.teachingClass.id")
    List <CountDomain> countByTeachingClass(@Param(value = "teachingClassList") List <TeachingClass> teachingClassList);


    @Query("select distinct t.teachingClass.id from  #{#entityName} t where t.student = :student")
    Set <Long> findTeachingClassIdByStudent(@Param(value = "student") User student);


    @Query("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassAndClasses(t.teachingClass.id, t.teachingClass.name, t.student.classes.id, t.student.classes.name) from  #{#entityName} t where t.student.classes.id in (:classesIds) and t.teachingClass.semester = :semester")
    List <TeachingclassAndClasses> findTeachingClassIdNameByClassesAndSemester(@Param(value = "classesIds") Set <Long> classesIds, @Param(value = "semester") Semester semester);

//    @Modifying
//    @Query("delete from  #{#entityName} t where t.teachingClass = :teachingClass and t.student.classes in (:classes)")
//    void deleteByTeachingClassAndClassesIn(@Param(value = "teachingClass") TeachingClass teachingClass, @Param(value = "classes") Set<Classes> classes);

    List<TeachingClassStudents> findByTeachingClassAndStudent_ClassesIn(TeachingClass teachingClass, Set<Classes> classes);


    @Query("select distinct t.teachingClass.id from  #{#entityName} t where t.student = :student and t.semester = :semester")
    Set <Long> findTeachingClassIdByStudentAndSemester(@Param(value = "student") User student, @Param(value = "semester") Semester semester);
}