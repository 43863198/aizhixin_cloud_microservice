package com.aizhixin.cloud.orgmanager.classschedule.repository;

import com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianDaySchoolTimeTableDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianSchoolTimeDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianWeekSchoolTimeTableDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeacherCourseScheduleDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolTimeTable;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SchoolTimeTableRepository extends JpaRepository<SchoolTimeTable, Long> {
    @Modifying
    @Query("delete from  #{#entityName} t where t.teachingClass = :teachingClass")
    void deleteByTeachingClass(@Param(value = "teachingClass") TeachingClass teachingClass);

    List<SchoolTimeTable> findByTeachingClassOrderByDayOfWeek(TeachingClass teachingClass);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianSchoolTimeDomain(t.teachingClass.id, t.period.id, t.periodNo, t.periodNum, t.classroom) from  #{#entityName} t where t.semester = :semester and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.dayOfWeek = :dayOfWeek and t.singleOrDouble in (:singleOrDoubles)")
    List<DianDianSchoolTimeDomain> findBySemesterAndWeekAndDayOfWeek(@Param(value = "semester") Semester semester, @Param(value = "weekNo") Integer weekNo,
        @Param(value = "dayOfWeek") Integer dayOfWeek, @Param(value = "singleOrDoubles") Set<Integer> singleOrDoubles);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianWeekSchoolTimeTableDomain(t.teachingClass.id, t.teachingClass.name,t.teachingClass.code, t.startWeek.id, t.period.id, t.periodNum, t.dayOfWeek, t.classroom) from  #{#entityName} t  where t.teachingClass.id in(:teachingClassIds) and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.singleOrDouble in (:singleOrDoubles)")
    List<DianDianWeekSchoolTimeTableDomain> findBySemesterAndWeek(@Param(value = "teachingClassIds") Set<Long> teachingClassIds, @Param(value = "weekNo") Integer weekNo,
        @Param(value = "singleOrDoubles") Set<Integer> singleOrDoubles);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianDaySchoolTimeTableDomain(t.teachingClass.id, t.startWeek.id, t.period.id, t.periodNum, t.dayOfWeek, t.classroom) from  #{#entityName} t  where t.teachingClass.id in(:teachingClassIds) and t.semester = :semester and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.dayOfWeek = :dayOfWeek and t.singleOrDouble in (:singleOrDoubles)")
    List<DianDianDaySchoolTimeTableDomain> findBySemesterAndDayOfWeek(@Param(value = "teachingClassIds") Set<Long> teachingClassIds, @Param(value = "semester") Semester semester,
        @Param(value = "weekNo") Integer weekNo, @Param(value = "dayOfWeek") Integer dayOfWeek, @Param(value = "singleOrDoubles") Set<Integer> singleOrDoubles);

    @Modifying
    @Query("delete from  #{#entityName} t where t.orgId = :orgId and t.semester = :semester")
    void deleteByOrgIdAndSemester(@Param(value = "orgId") Long orgId, @Param(value = "semester") Semester semester);

    long countByTeachingClass(TeachingClass teachingClass);

    @Modifying
    @Query("delete from  #{#entityName} t where t.teachingClass in (:teachingClassList)")
    void deleteByTeachingClassIn(@Param(value = "teachingClassList") List<TeachingClass> teachingClassList);

    @Query("select t.teachingClass.id from  #{#entityName} t where t.teachingClass.id in (:teachingclassIds)")
    List<Long> findByTeachingclassIds(@Param(value = "teachingclassIds") Set<Long> teachingclassIds);

    // @Query("select count(t.id) from #{#entityName} t where t.teachingClass = :teachingClass and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.dayOfWeek = :dayOfWeek
    // and (t.periodNo >= :startPeriodNo and (t.periodNo + t.periodNum - 1 < :endPeriodNo)) and t.singleOrDouble in (:singleOrDoubles)")
    // long countByFullTeachingClassAndWeekAndDayOfWeek(@Param(value = "teachingClass")TeachingClass teachingClass, @Param(value = "weekNo")Integer weekNo, @Param(value =
    // "dayOfWeek")Integer dayOfWeek, @Param(value = "singleOrDoubles")Set<Integer> singleOrDoubles, @Param(value = "startPeriodNo")Integer startPeriodNo, @Param(value =
    // "endPeriodNo")Integer endPeriodNo);

    @Query("select count(t.id) from  #{#entityName} t where t.teachingClass = :teachingClass and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.dayOfWeek = :dayOfWeek and t.singleOrDouble in (:singleOrDoubles) and t.periodNo = :periodNo and t.periodNum = :periodNum")
    long countByTeachingClassAndWeekAndDayOfWeek(@Param(value = "teachingClass") TeachingClass teachingClass, @Param(value = "weekNo") Integer weekNo,
        @Param(value = "dayOfWeek") Integer dayOfWeek, @Param(value = "singleOrDoubles") Set<Integer> singleOrDoubles, @Param(value = "periodNo") Integer periodNo,
        @Param(value = "periodNum") Integer periodNum);

    @Query("select t from  #{#entityName} t where t.semester = :semester and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.dayOfWeek = :dayOfWeek and (t.periodNo >= :startPeriodNo and (t.periodNo + t.periodNum - 1 < :endPeriodNo)) and t.singleOrDouble in (:singleOrDoubles)")
    List<SchoolTimeTable> findBySemesterAndWeekAndDayOfWeek(@Param(value = "semester") Semester semester, @Param(value = "weekNo") Integer weekNo,
        @Param(value = "dayOfWeek") Integer dayOfWeek, @Param(value = "singleOrDoubles") Set<Integer> singleOrDoubles, @Param(value = "startPeriodNo") Integer startPeriodNo,
        @Param(value = "endPeriodNo") Integer endPeriodNo);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianDaySchoolTimeTableDomain(t.teachingClass.id, t.dayOfWeek, t.periodNo, t.periodNum, t.classroom) from  #{#entityName} t where t.teachingClass in (:teachingClassList) and t.startWeekNo <= :weekNo and t.endWeekNo >= :weekNo and t.dayOfWeek = :dayOfWeek and ((t.periodNo <= :startPeriodNo and t.periodNo + t.periodNum - 1 >= :startPeriodNo) or (t.periodNo <= :endPeriodNo and t.periodNo + t.periodNum - 1 >= :endPeriodNo)) and t.singleOrDouble in (:singleOrDoubles)")
    List<DianDianDaySchoolTimeTableDomain> findTeachingClassByTeachingclassAndDayAndPeriodRanage(@Param(value = "teachingClassList") Set<TeachingClass> teachingClassList,
        @Param(value = "weekNo") Integer weekNo, @Param(value = "dayOfWeek") Integer dayOfWeek, @Param(value = "singleOrDoubles") Set<Integer> singleOrDoubles,
        @Param(value = "startPeriodNo") Integer startPeriodNo, @Param(value = "endPeriodNo") Integer endPeriodNo);

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeacherCourseScheduleDomain(t.teachingClass.id, t.teachingClass.name, t.teachingClass.course.id, t.teachingClass.course.name, t.startWeekNo, t.endWeekNo, t.singleOrDouble, t.dayOfWeek, t.periodNo, t.periodNum, t.classroom) from  #{#entityName} t where t.teachingClass in (:teachingClassSet) ")
    List<TeacherCourseScheduleDomain> findByTeachingClassIn(@Param(value = "teachingClassSet") Set<TeachingClass> teachingClassSet);

    @Query("select t from #{#entityName} t where t.teachingClass in (:teachingClassSet) ")
    List<SchoolTimeTable> findByTeachingClassIn1(@Param(value = "teachingClassSet") Set<TeachingClass> teachingClassSet);

}