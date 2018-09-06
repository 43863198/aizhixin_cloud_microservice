package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.StudentLeaveSchedule;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface StudentLeaveScheduleRepository extends
        JpaRepository<StudentLeaveSchedule, Long> {
//    @Modifying
//    @Query(value = "SELECT sls.studentId from com.aizhixin.cloud.dd.rollcall.entity.StudentLeaveSchedule sls where sls.schedule.Id = :scheduleId ")
//    List<Long> findStudentIdBySchedule(@Param("scheduleId") Long
//                                               scheduleId);


    List<StudentLeaveSchedule> findAllByScheduleId(Long scheduleId);

    List<StudentLeaveSchedule> findByStudentIdAndTeacherIdAndCourseIdAndRequesDateAndRequestPeriodIdAndDeleteFlag(Long studentId, Long teacherId, Long courseId, Date requestDate, Long requestperiodId, Integer deleteFlag);

    List<StudentLeaveSchedule> findByTeacherIdAndCourseIdAndRequesDateAndRequestPeriodIdAndDeleteFlag(Long teacherId, Long courseId, java.util.Date requestDate, Long requestperiodId, Integer deleteFlag);

    List<StudentLeaveSchedule> findByScheduleIdAndStudentId(Long scheduleId, Long studentId);

    @Modifying
    @Query(value = "SELECT sls from com.aizhixin.cloud.dd.rollcall.entity.StudentLeaveSchedule sls where sls.teacherId = :teacherId and sls.deleteFlag =:deleteFlag ")
    List<StudentLeaveSchedule> findByTeacherIdAndDeleteFlagOrderByLastModifiedDateDesc(@Param("teacherId") Long teacherId, @Param("deleteFlag") Integer deleteFlag);

    @Modifying
    @Query(value = "SELECT sls from com.aizhixin.cloud.dd.rollcall.entity.StudentLeaveSchedule sls where sls.teacherId = :teacherId and sls.deleteFlag =:deleteFlag order by sls.lastModifiedDate desc")
    List<StudentLeaveSchedule> findByTeacherIdAndDeleteFlag(@Param("teacherId") Long teacherId, @Param("deleteFlag") Integer deleteFlag);

}
