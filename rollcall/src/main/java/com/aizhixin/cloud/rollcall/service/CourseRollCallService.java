package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.core.DataValidity;
import com.aizhixin.cloud.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.rollcall.repository.CourseRollCallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseRollCallService {

    @Autowired
    private CourseRollCallRepository courseRollCallRepository;

    @Transactional(readOnly = true)
    public CourseRollCall get(Long courseId, Long teacherId) {
        return courseRollCallRepository.findByCourseIdAndTeacherIdAndDeleteFlag(courseId, teacherId, DataValidity.VALID.getState());
    }
}
