package com.aizhixin.cloud.dd.rollcall.service;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.rollcall.entity.StudentLeaveSchedule;
import com.aizhixin.cloud.dd.rollcall.repository.StudentLeaveScheduleRepository;

@Service
@Transactional
public class StudentLeaveScheduleService {

    @Autowired
    private StudentLeaveScheduleRepository studentLeaveScheduleRepository;

    @Autowired
    private ScheduleService scheduleService;

    public void save(StudentLeaveSchedule studentLeaveSchedule) {
        studentLeaveScheduleRepository.save(studentLeaveSchedule);
    }

    public List <Long> findStudentIdByScheduleId(Long scheduleId) {
        return findStudentIdByScheduleId(scheduleService.findOne(scheduleId));

    }

    public List <Long> findStudentIdByScheduleId(Schedule schedule) {
//        return studentLeaveScheduleRepository.findStudentIdBySchedule(scheduleId);
        List <Long> stList = null;
        List <StudentLeaveSchedule> list = studentLeaveScheduleRepository.findAllByScheduleId(schedule.getId());
        if (null != list && list.size() > 0) {
            stList = new ArrayList();
            for (StudentLeaveSchedule sl : list) {
                stList.add(sl.getStudentId());
            }
        }
        return stList;

    }

    public Boolean findByScheduleIdAndStudentId(Long scheduleId, Long studentId) {
        List <StudentLeaveSchedule> list = studentLeaveScheduleRepository.findByScheduleIdAndStudentId(scheduleId, studentId);
        if (null != list && list.size() > 0) {
            return true;
        }
        return false;
    }

    public List <Long> findStudentIdByScheduleIdNoCache(Long scheduleId) {
        return null;// studentLeaveScheduleRepository.findStudentIdByScheduleId(scheduleId);

    }

}
