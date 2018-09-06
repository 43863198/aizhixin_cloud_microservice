package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findByTeacherIdAndTeachDateAndDeleteFlag(Pageable pageable, Long teacherId, String teachDate, Integer deleteFlag);

    List<Schedule> findByTeacherIdAndCourseIdAndTeachDateAndDeleteFlag(Long teacherId, Long courseId, String teachDate, Integer deleteFlag);

    List<Schedule> findByTeachDateAndDeleteFlagAndTeachingclassIdIn(String teachDate, Integer deleteFlag, Set teachingclassIds);

    Page<Schedule> findByTeachingclassIdAndTeachDateAndDeleteFlag(Pageable pageable, Long teachingclassId, String teachDate, Integer deleteFlag);

    List<Schedule> findByteachingclassIdInAndTeachDateAndDeleteFlag(Set teachingclassIds, String teachDate, Integer deleteFlag, Sort sort);

    List<Schedule> findByTeachingclassIdAndTeachDateAndPeriodNoAndDeleteFlag(Long teachingClassId, String teachDate, Integer periodNo, Integer deleteFlag);

    @Modifying
    @Query("delete from com.aizhixin.cloud.dd.rollcall.entity.Schedule r where r.teacherId = :teacherId and r.courseId =:courseId and r.teachDate =:teachDate and r.periodId=:periodId")
    void deleteByTeacherIdAndCourseIdAndTeachDateAndPeriodId(@Param(value = "teacherId") Long teacherId, @Param(value = "courseId") Long courseId,
        @Param(value = "teachDate") String teachDate, @Param(value = "periodId") Long periodId);

    List<Schedule> findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndDeleteFlag(@Param(value = "teacherId") Long teacherId, @Param(value = "courseId") Long courseId,
        @Param(value = "teachDate") String teachDate, @Param(value = "periodId") Long periodId, @Param(value = "deleteFlag") Integer deleteFlag);

    List<Schedule> findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndPeriodNumAndDeleteFlag(@Param(value = "teacherId") Long teacherId, @Param(value = "courseId") Long courseId,
        @Param(value = "teachDate") String teachDate, @Param(value = "periodId") Long periodId, @Param(value = "periodNum") Integer periodNum,
        @Param(value = "deleteFlag") Integer deleteFlag);

    List<Schedule> findAllByTeacherIdAndWeekId(Long teacherId, Long weekId, Sort sort);

    List<Schedule> findAllByTeacherId(Long teacherId, Sort sort);

    Schedule findByIdAndDeleteFlag(Long id, Integer deleteFlag);

    List<Schedule> findAllByweekIdAndTeachingclassIdIn(Long weekId, Set<Long> teachingClassIds, Sort sort);

    List<Schedule> findDistinctCourseIdByTeacherIdAndSemesterIdAndDeleteFlag(Long teacherId, Long semesterId, Integer deteleFlag);

    List<Schedule> findAllByTeacherIdAndDeleteFlag(Long teacherId, Integer deteleFlag);

    List<Schedule> findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(Long teachingclassId, String teachDate, Integer periodNu, Integer periodNum,
        Integer deleteFlag);

    List<Schedule> findAllByOrganIdAndDeleteFlag(Long organId, Integer deleteFlag);

    List<Schedule> findAllByOrganIdAndTeachDateAndDeleteFlag(Long organId, String teachDate, Integer deleteFlag);

    List<Schedule> findByIdInAndDeleteFlag(List<Long> ids, Integer deleteFlag);
}
