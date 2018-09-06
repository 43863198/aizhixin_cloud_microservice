package com.aizhixin.cloud.rollcall.repository;

import com.aizhixin.cloud.rollcall.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.rollcall.entity.StudentLeaveSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StudentLeaveScheduleRepository extends JpaRepository<StudentLeaveSchedule, Long> {

    @Query("select new  com.aizhixin.cloud.rollcall.common.domain.IdIdNameDomain(t.scheduleId, t.studentId) from #{#entityName} t where t.scheduleId in (:scheduleIds)")
    List<IdIdNameDomain> findAllByScheduleId(@Param(value = "scheduleIds") Set<Long> scheduleIds);

    List<StudentLeaveSchedule> findByTeacherIdAndCourseIdAndRequesDateAndRequestPeriodIdAndDeleteFlag(Long teacherId, Long courseId, java.util.Date requestDate, Long requestperiodId, Integer deleteFlag);
}