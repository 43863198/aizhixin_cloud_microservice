package com.aizhixin.cloud.rollcall.repository;

import com.aizhixin.cloud.rollcall.entity.CourseRollCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRollCallRepository extends JpaRepository<CourseRollCall, Long> {
	CourseRollCall findByCourseIdAndTeacherIdAndDeleteFlag(Long courseId, Long teacherId, Integer deleteFlag);
}
