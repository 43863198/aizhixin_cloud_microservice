package com.aizhixin.cloud.dd.rollcall.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.CourseRollCallRepository;

/**
 * @author meihua.li
 */
@Service
@Transactional
public class CourseRollCallService {

    private final Logger log = LoggerFactory.getLogger(CourseRollCallService.class);

    @Autowired
    private CourseRollCallRepository courseRollCallRepository;

    public CourseRollCall get(Long courseId, Long teacherId) {
        return courseRollCallRepository.findByCourseIdAndTeacherIdAndDeleteFlag(courseId, teacherId, DataValidity.VALID.getState());
    }

    public void closeRollCall(Long courseId, Long teacherId) {
        courseRollCallRepository.updateIsOpenRollCall(courseId, teacherId, CourseRollCallConstants.CLOSE_ROLLCALL);
    }

    public void openRollCall(Long courseId, Long teacherId) {
        courseRollCallRepository.updateIsOpenRollCall(courseId, teacherId, CourseRollCallConstants.OPEN_ROLLCALL);
    }

    public void updateRollCall(Long courseId, Long teacherId, String rollCallType, int lateTime, String isOpen) {
        courseRollCallRepository.updateRollCall(courseId, teacherId, rollCallType, lateTime, isOpen);
    }

    public void updateRollCall(Long courseId, Long teacherId, String rollCallType, int lateTime, int absenteeismTime, String isOpen) {
        courseRollCallRepository.updateRollCall(courseId, teacherId, rollCallType, lateTime, absenteeismTime, isOpen);
    }

    public void saveRollCall(CourseRollCall courseRollCall) {
        courseRollCallRepository.save(courseRollCall);
    }
}
