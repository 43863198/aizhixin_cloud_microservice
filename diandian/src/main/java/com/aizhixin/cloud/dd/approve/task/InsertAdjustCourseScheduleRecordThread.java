package com.aizhixin.cloud.dd.approve.task;

import com.aizhixin.cloud.dd.approve.domain.AdjustCourseScheduleRecordDomain;
import com.aizhixin.cloud.dd.approve.entity.AdjustCourseScheduleRecord;
import com.aizhixin.cloud.dd.approve.services.AdjustCourseScheduleRecordService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertAdjustCourseScheduleRecordThread implements Runnable {
    private AdjustCourseScheduleRecordService adjustCourseScheduleRecordService;
    private Long teachingClassId;
    private AdjustCourseScheduleRecordDomain adjustCourseScheduleRecordDomain;
    private OrgManagerRemoteClient orgManagerRemoteService;
    private Long userId;
    private InitScheduleService initScheduleService;

    public InsertAdjustCourseScheduleRecordThread(AdjustCourseScheduleRecordService adjustCourseScheduleRecordService, Long teachingClassId, AdjustCourseScheduleRecordDomain adjustCourseScheduleRecordDomain, OrgManagerRemoteClient orgManagerRemoteService, Long userId, InitScheduleService initScheduleService) {
        this.adjustCourseScheduleRecordService = adjustCourseScheduleRecordService;
        this.teachingClassId = teachingClassId;
        this.adjustCourseScheduleRecordDomain = adjustCourseScheduleRecordDomain;
        this.orgManagerRemoteService = orgManagerRemoteService;
        this.userId = userId;
        this.initScheduleService = initScheduleService;
    }

    @Override
    public void run() {
        List<AdjustCourseScheduleRecord> adjustCourseScheduleRecordList = new ArrayList<>();
        String json = orgManagerRemoteService.teachingClassGetById(teachingClassId);
        Map mapData = JSON.parseObject(json, Map.class);
        String courseName = null;
        String teachingClassName = null;
        if (null != mapData && null != mapData.get("courseName")) {
            courseName = mapData.get("courseName").toString();
        }
        if (null != mapData && null != mapData.get("name")) {
            teachingClassName = mapData.get("name").toString();
        }
        List<Map<String, Object>> studentList = orgManagerRemoteService.getTeachingclassStudents(teachingClassId);
        Map<String, Object> teacherMap = orgManagerRemoteService.getTeachingclassTeachers(teachingClassId);
        Long teacherId = null;
        String teacherName = null;
        if (null != teacherMap && null != teacherMap.get("data")) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) teacherMap.get("data");
            if (mapList != null && 0 < mapList.size()) {
                teacherId = Long.valueOf(mapList.get(0).get("id").toString());
                teacherName = mapList.get(0).get("name").toString();
                AdjustCourseScheduleRecord adjustCourseScheduleRecord = new AdjustCourseScheduleRecord();
                BeanUtils.copyProperties(adjustCourseScheduleRecordDomain, adjustCourseScheduleRecord);
                adjustCourseScheduleRecord.setTeachingClassId(teachingClassId);
                adjustCourseScheduleRecord.setUserId(teacherId);
                adjustCourseScheduleRecord.setTeacherName(teacherName);
                adjustCourseScheduleRecord.setCourseName(courseName);
                adjustCourseScheduleRecord.setCreatedBy(userId);
                adjustCourseScheduleRecord.setTeachingClassName(teachingClassName);
                adjustCourseScheduleRecordList.add(adjustCourseScheduleRecord);

            }
        }
        if (null != studentList && 0 <= studentList.size()) {
            for (Map<String, Object> map : studentList) {
                if (null != map.get("id")) {
                    AdjustCourseScheduleRecord adjustCourseScheduleRecord = new AdjustCourseScheduleRecord();
                    BeanUtils.copyProperties(adjustCourseScheduleRecordDomain, adjustCourseScheduleRecord);
                    adjustCourseScheduleRecord.setUserId(Long.valueOf(map.get("id").toString()));
                    adjustCourseScheduleRecord.setTeacherName(teacherName);
                    adjustCourseScheduleRecord.setCourseName(courseName);
                    adjustCourseScheduleRecord.setTeachingClassId(teachingClassId);
                    adjustCourseScheduleRecord.setCreatedBy(userId);
                    adjustCourseScheduleRecord.setTeachingClassName(teachingClassName);
                    adjustCourseScheduleRecordList.add(adjustCourseScheduleRecord);
                }
            }
        }
        if (!adjustCourseScheduleRecordList.isEmpty()) {
            adjustCourseScheduleRecordService.saveList(adjustCourseScheduleRecordList);
        }
        initScheduleService.refStuTodayScheduleByTeachingClass(teachingClassId);
    }
}
