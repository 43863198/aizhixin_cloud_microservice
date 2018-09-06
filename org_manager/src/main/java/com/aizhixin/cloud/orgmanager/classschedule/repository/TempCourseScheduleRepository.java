package com.aizhixin.cloud.orgmanager.classschedule.repository;

import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TempCourseSchedule;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TempCourseScheduleRepository extends JpaRepository<TempCourseSchedule, Long> {
//    List<TempCourseSchedule> findByAdjustId(Long adjustId);

    List<TempCourseSchedule> findByTeachingClassInAndEventDateAndAdjustTypeAndDeleteFlag(Set<TeachingClass> teachingclasses, String eventDate, Integer adjustType, Integer deleteFlag);

    @Query("select count(t.id) from #{#entityName} t where t.teachingClass in (:teachingclasses) and t.eventDate = :eventDate and t.adjustType = :adjustType and t.deleteFlag = :deleteFlag and ((t.periodNo <= :minPeriodNo and t.periodNo + t.periodNum - 1 >= :minPeriodNo)  or (t.periodNo <= :maxPeriodNo and t.periodNo + t.periodNum - 1 >= :maxPeriodNo))")
    long countByTeachingClassInAndEventDateAndAdjustTypeAndDeleteFlag(@Param(value = "teachingclasses") Set<TeachingClass> teachingclasses, @Param(value = "eventDate") String eventDate, @Param(value = "adjustType") Integer adjustType, @Param(value = "minPeriodNo") Integer minPeriodNo, @Param(value = "maxPeriodNo") Integer maxPeriodNo, @Param(value = "deleteFlag") Integer deleteFlag);

    List<TempCourseSchedule> findByTeachingClass_idInAndWeekNoAndDeleteFlag(Set<Long> teachingclassIds, Integer weekNo, Integer deleteFlag);

    List<TempCourseSchedule> findByTeachingClass_idInAndWeekNoAndDayOfWeekAndDeleteFlag(Set<Long> teachingclassIds, Integer weekNo, Integer dayOfWeek, Integer deleteFlag);

    List<TempCourseSchedule> findByOrgIdAndSemesterAndWeekNoAndDayOfWeekAndDeleteFlag(Long orgId, Semester semester, Integer weekNo, Integer dayOfWeek, Integer deleteFlag);

    @Query("select t.teachingClass.id from #{#entityName} t where t.teachingClass.id in (:teachingclassIds) and t.adjustType = :adjustType and t.deleteFlag = :deleteFlag")
    Set<Long> findTeachingclassIdByTeachingClassIdsAndAdjustType(@Param(value = "teachingclassIds") Set<Long> teachingclassIds, @Param(value = "adjustType") Integer adjustType, @Param(value = "deleteFlag") Integer deleteFlag);


//    @Query("select count(t.id) from #{#entityName} t where t.teachingClass.id = :teachingclassId and t.eventDate = :eventDate and t.adjustType = :adjustType and t.deleteFlag = :deleteFlag and t.periodNo = :periodNo and t.periodNum = :periodNum")
    long countByTeachingClass_idAndEventDateAndAdjustTypeAndPeriodNoAndPeriodNumAndDeleteFlag(Long teachingclassId, String eventDate, Integer adjustType, Integer periodNo, Integer periodNum, Integer deleteFlag);

//    @Query("select t from #{#entityName} t where t.teachingClass = :teachingclass and t.eventDate = :eventDate and t.adjustType = :adjustType and t.deleteFlag = :deleteFlag and t.periodNo = :periodNo and t.periodNum = :periodNum order by t.id")
    List<TempCourseSchedule> findByTeachingClassAndEventDateAndAdjustTypeAndPeriodNoAndPeriodNumAndDeleteFlag(TeachingClass teachingclass, String eventDate, Integer adjustType, Integer periodNo, Integer periodNum, Integer deleteFlag);

    @Query("select t from #{#entityName} t where t.teachingClass in (:teachingclasses) and t.eventDate = :eventDate and t.adjustType = :adjustType and t.deleteFlag = :deleteFlag and t.periodNo = :periodNo and t.periodNum = :periodNum order by t.id")
    List<TempCourseSchedule> findByAllTeachingClassAndEventDateAndAdjustTypeAndPeriodNoAndPeriodNum(@Param(value = "teachingclasses") Set<TeachingClass> teachingclasses, @Param(value = "eventDate") String eventDate, @Param(value = "adjustType") Integer adjustType, @Param(value = "periodNo") Integer periodNo, @Param(value = "periodNum") Integer periodNum, @Param(value = "deleteFlag") Integer deleteFlag);

    List<TempCourseSchedule> findByTeachingClassInAndDeleteFlag(List<TeachingClass> teachingclasses, Integer deleteFlag);
}
