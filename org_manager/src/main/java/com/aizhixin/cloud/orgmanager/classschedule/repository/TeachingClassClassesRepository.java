package com.aizhixin.cloud.orgmanager.classschedule.repository;


import com.aizhixin.cloud.orgmanager.classschedule.domain.ClassesCollegeDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassAndClasses;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassClasses;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface TeachingClassClassesRepository extends JpaRepository<TeachingClassClasses, Long> {
    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.ClassesCollegeDomain(t.teachingClass.id,t.classes.id, t.classes.name,t.classes.college.id,t.classes.college.name) from  #{#entityName} t where t.teachingClass.id in (:teachingClassIds)")
    List<ClassesCollegeDomain> findSimpleClassesByTeachingClassIds(@Param(value = "teachingClassIds") Set teachingClassIds);

    @Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(t.classes.id, t.classes.name) from  #{#entityName} t where t.teachingClass = :teachingClass")
    List<IdNameDomain> findSimpleClassesByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);


    Long countByTeachingClassAndClassesIn(TeachingClass teachingClass, List<Classes> classes);

    List<TeachingClassClasses> findByTeachingClassAndClasses(TeachingClass teachingClass, Classes classes);

    List<TeachingClassClasses> findByClasses(Classes classes);

    Long countByTeachingClass(TeachingClass teachingClass);

    @Query("select t.classes from  #{#entityName} t where t.teachingClass = :teachingClass")
    List<Classes> findClassesByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);

    @Query("select t.classes from  #{#entityName} t where t.teachingClass.id = :teachingClassId")
    List<Classes> findClassesByTeachingClassId(@Param(value = "teachingClassId") Long teachingClassId);

    @Query("select distinct t.teachingClass.id from  #{#entityName} t where t.classes = :classes and t.semester = :semester")
    Set<Long> findTeachingClassByClasses(@Param(value = "classes") Classes classes, @Param(value = "semester") Semester semester);

    List<TeachingClassClasses> findByTeachingClass(TeachingClass teachingClass);

    @Query("select distinct t.teachingClass from  #{#entityName} t where t.classes=:classes and t.teachingClass.course in (:courses) and t.teachingClass.semester=:semester")
    List<TeachingClass> findByTeachingClassByClassesAndCourses(@Param(value = "classes") Classes classes, @Param(value = "courses") List<Course> courses, @Param(value = "semester") Semester semester);

    @Modifying
    @Query("delete from  #{#entityName} t where t.teachingClass in (:teachingClassList)")
    void deleteByTeachingClassIn(@Param(value = "teachingClassList") List<TeachingClass> teachingClassList);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.teachingClass.id, t.teachingClass.name, t.teachingClass.code, t.teachingClass.course.id, t.teachingClass.course.name) from  #{#entityName} t where t.classes = :classes and t.semester = :semester")
    List<TeachingClassDomain> findTeachingClassByClassesAndSemester(@Param(value = "classes") Classes classes, @Param(value = "semester") Semester semester);

    @Query("select distinct t.teachingClass from  #{#entityName} t where t.teachingClass.orgId=:orgId and t.teachingClass.semester=:semester")
    List<TeachingClass> findAllTeachingClass(@Param(value = "orgId") Long orgId, @Param(value = "semester") Semester semester);

    @Query("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassAndClasses(t.teachingClass.id, t.teachingClass.name, t.classes.id, t.classes.name) from  #{#entityName} t where t.classes.id in (:classesIds) and t.semester = :semester")
    List<TeachingclassAndClasses> findTeachingClassIdNameByClassesAndSemester(@Param(value = "classesIds") Set<Long> classesIds, @Param(value = "semester") Semester semester);
}