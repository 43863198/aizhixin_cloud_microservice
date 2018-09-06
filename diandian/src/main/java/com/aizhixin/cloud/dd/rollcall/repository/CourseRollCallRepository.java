package com.aizhixin.cloud.dd.rollcall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.dd.rollcall.entity.CourseRollCall;

public interface CourseRollCallRepository extends JpaRepository<CourseRollCall, Long> {

    CourseRollCall findByCourseIdAndTeacherIdAndDeleteFlag(Long courseId, Long teacherId, Integer deleteFlag);

    @Modifying
    @Query(value = "update #{#entityName} c set c.isOpen = :isOpen where c.courseId = :courseId and c.teacherId = :teacherId")
    void updateIsOpenRollCall(@Param(value = "courseId") Long courseId, @Param(value = "teacherId") Long teacherId, @Param(value = "isOpen") String isOpen);

    @Modifying
    @Query(value = "update #{#entityName} c set c.rollCallType = :rollCallType,c.lateTime =:lateTime ,c.isOpen = :isOpen where c.courseId = :courseId and c.teacherId = :teacherId")
    void updateRollCall(@Param(value = "courseId") Long courseId,
                        @Param(value = "teacherId") Long teacherId,
                        @Param(value = "rollCallType") String rollCallType,
                        @Param(value = "lateTime") Integer lateTime,
                        @Param(value = "isOpen") String isOpen);

    @Modifying
    @Query(value = "update #{#entityName} c set c.rollCallType = :rollCallType,c.lateTime =:lateTime, c.absenteeismTime=:absenteeismTime, c.isOpen = :isOpen where c.courseId = :courseId and c.teacherId = :teacherId")
    void updateRollCall(@Param(value = "courseId") Long courseId,
                        @Param(value = "teacherId") Long teacherId,
                        @Param(value = "rollCallType") String rollCallType,
                        @Param(value = "lateTime") Integer lateTime,
                        @Param(value = "absenteeismTime") Integer absenteeismTime,
                        @Param(value = "isOpen") String isOpen);
}
