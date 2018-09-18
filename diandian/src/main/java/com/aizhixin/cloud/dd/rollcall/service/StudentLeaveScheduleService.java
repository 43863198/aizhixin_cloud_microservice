package com.aizhixin.cloud.dd.rollcall.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.repository.LeaveRepository;
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
    private LeaveRepository leaveRepository;
    @Autowired
    private ScheduleService scheduleService;

    public void save(StudentLeaveSchedule studentLeaveSchedule) {
        studentLeaveScheduleRepository.save(studentLeaveSchedule);
    }

    public List<Long> findStudentIdByScheduleId(Long scheduleId) {
        return findStudentIdByScheduleId(scheduleService.findOne(scheduleId));

    }

    public List<Long> findStudentIdByScheduleId(Schedule schedule) {
        List<Long> stList = null;
        List<StudentLeaveSchedule> list = studentLeaveScheduleRepository.findAllByScheduleId(schedule.getId());
        if (null != list && list.size() > 0) {
            stList = new ArrayList();
            for (StudentLeaveSchedule sl : list) {
                stList.add(sl.getStudentId());
            }
        }
        return stList;
    }

    public List<Long> findStudentIdByScheduleId(Schedule schedule, Date date) {
        List<Long> stList = null;
        String day = DateFormatUtil.formatShort(date);
        List<StudentLeaveSchedule> list = studentLeaveScheduleRepository.findAllByScheduleId(schedule.getId());
        if (null != list && list.size() > 0) {
            stList = new ArrayList();
            for (StudentLeaveSchedule sl : list) {
                Leave leave = leaveRepository.findOne(sl.getLeaveId());
                if (leave.getLeavePublic() == LeaveConstants.TYPE_PR) {
                    if (day.equals(DateFormatUtil.formatShort(leave.getStartTime()))) {
                        if (date.getTime() >= leave.getStartTime().getTime()) {
                            stList.add(sl.getStudentId());
                        }
                    } else if (day.equals(DateFormatUtil.formatShort(leave.getEndTime()))) {
                        if (date.getTime() <= leave.getEndTime().getTime()) {
                            stList.add(sl.getStudentId());
                        }
                    } else {
                        stList.add(sl.getStudentId());
                    }
                }
            }
        }
        return stList;
    }

    public Boolean findByScheduleIdAndStudentId(Long scheduleId, Long studentId) {
        List<StudentLeaveSchedule> list = studentLeaveScheduleRepository.findByScheduleIdAndStudentId(scheduleId, studentId);
        if (null != list && list.size() > 0) {
            return true;
        }
        return false;
    }

    public List<Long> findStudentIdByScheduleIdNoCache(Long scheduleId) {
        return null;// studentLeaveScheduleRepository.findStudentIdByScheduleId(scheduleId);

    }

}
