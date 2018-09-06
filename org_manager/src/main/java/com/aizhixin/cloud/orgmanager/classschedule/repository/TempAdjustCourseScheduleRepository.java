package com.aizhixin.cloud.orgmanager.classschedule.repository;

import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TempAdjustCourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TempAdjustCourseScheduleRepository extends JpaRepository<TempAdjustCourseSchedule, Long> {
//    List<TempAdjustCourseSchedule> findByAdjustId(Long adjustId);

    List<TempAdjustCourseSchedule> findByTeachingClass_idInAndWeekNoAndValidStatusAndApprovalStatusAndDeleteFlag(Set<Long> teachingclassIds, Integer weekNo, Integer validStatus, Integer approvalStatus, Integer deleteFlag);

    List<TempAdjustCourseSchedule> findByTeachingClass_idInAndWeekNoAndDayOfWeekAndValidStatusAndApprovalStatusAndDeleteFlag(Set<Long> teachingclassIds, Integer weekNo, Integer dayOfWeek, Integer validStatus, Integer approvalStatus, Integer deleteFlag);

    List<TempAdjustCourseSchedule> findByTeachingClassAndWeekNoAndDayOfWeekAndPeriodNoAndPeriodNumAndAdjustTypeAndDeleteFlag(TeachingClass teachingClass, Integer weekNo, Integer dayOfWeek, Integer periodNo, Integer periodNum, Integer adjustType, Integer deleteFlag);
}
