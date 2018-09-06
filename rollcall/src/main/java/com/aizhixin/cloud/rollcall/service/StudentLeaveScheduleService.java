package com.aizhixin.cloud.rollcall.service;


import com.aizhixin.cloud.rollcall.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.rollcall.repository.StudentLeaveScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class StudentLeaveScheduleService {

    @Autowired
    private StudentLeaveScheduleRepository studentLeaveScheduleRepository;

    @Transactional (readOnly = true)
    public List<IdIdNameDomain> findLeaveStudentsByScheduleIds(Set<Long> scheduleIdSet) {
        return studentLeaveScheduleRepository.findAllByScheduleId(scheduleIdSet);
    }

    /**
     * 课堂id及对应请假学生的id列表
     * @param scheduleIds   课堂id
     * @return  课堂id及对应请假学生的id列表
     */
    @Transactional (readOnly = true)
    public Map<Long, Set <Long>> getScheduleLeaveStudents(Set <Long> scheduleIds) {
        Map <Long, Set <Long>> scheduleLeaveStudentIds = new HashMap<>();
        if (!scheduleIds.isEmpty()) {
            List <IdIdNameDomain> list = findLeaveStudentsByScheduleIds(scheduleIds);//课堂请假的学生列表
            for (IdIdNameDomain d : list) {
                Set <Long> stuIds = scheduleLeaveStudentIds.get(d.getLogicId());
                if (null == stuIds) {
                    stuIds = new HashSet<>();
                    scheduleLeaveStudentIds.put(d.getLogicId(), stuIds);
                }
                stuIds.add(d.getId());
            }
        }
        return scheduleLeaveStudentIds;
    }
}
