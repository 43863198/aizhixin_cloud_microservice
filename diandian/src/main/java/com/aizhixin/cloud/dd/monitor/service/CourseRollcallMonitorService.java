package com.aizhixin.cloud.dd.monitor.service;

import com.aizhixin.cloud.dd.common.domain.IdNameCode;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.CourseRollCallDTO;
import com.aizhixin.cloud.dd.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.dd.rollcall.service.CourseRollCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LIMH
 * @date 2017/12/14
 */
@Component
public class CourseRollcallMonitorService {

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private CourseRollCallService courseRollCallService;

    public List<CourseRollCallDTO> getCourseRollcall(Long teacherId) {
        // 获取课程列表信息
        List<IdNameCode> courseList = orgManagerRemoteService.getSemesterCourseSchedule(teacherId, null);

        List<CourseRollCallDTO> crcList = new ArrayList();
        CourseRollCallDTO courseRollCallDTO = null;
        // 组装数据
        for (IdNameCode domain : courseList) {
            courseRollCallDTO = new CourseRollCallDTO();
            CourseRollCall courseRollCall = courseRollCallService.get(domain.getId(), teacherId);
            courseRollCallDTO.setId(domain.getId());
            courseRollCallDTO.setCourseId(domain.getId());
            courseRollCallDTO.setCourseName(domain.getName());
            courseRollCallDTO.setReuser(10);

            if (null != courseRollCall) {
                courseRollCallDTO.setIsOpen(courseRollCall.getIsOpen());
                courseRollCallDTO.setLateTime(courseRollCall.getLateTime());
                courseRollCallDTO.setAbsenteeismTime(courseRollCall.getAbsenteeismTime());
                courseRollCallDTO.setRollCallTypeOrigin(courseRollCall.getRollCallType());
            } else {
                courseRollCallDTO.setIsOpen(CourseRollCallConstants.CLOSE_ROLLCALL);
                courseRollCallDTO.setLateTime(15);
                courseRollCallDTO.setAbsenteeismTime(0);
                courseRollCallDTO.setRollCallTypeOrigin(ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC);
            }

            crcList.add(courseRollCallDTO);
        }
        return crcList;
    }

    public List<IdNameDomain> getAllOrg() {
        return orgManagerRemoteService.findAllOrg();
    }

    public String listTeacherInfo(Long orgId, String teacherName, Integer pageSize, Integer pageNum) {
        List<IdNameDomain> list = new ArrayList<>();
        try {
            return orgManagerRemoteService.teacherList(orgId, null, teacherName, pageNum == null ? 1 : pageNum, pageSize == null ? 20 : pageSize);
        } catch (Exception e) {
        }
        return null;
    }
}
