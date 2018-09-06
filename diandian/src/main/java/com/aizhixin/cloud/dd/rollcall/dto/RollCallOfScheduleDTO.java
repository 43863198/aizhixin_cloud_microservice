package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/6/17.
 */
@ApiModel(description = "个人中心 我的考勤")
public class RollCallOfScheduleDTO {
    private Long scheduleId;

    private Long allCount;

    private String dayOfWeek;

    private String periodName;

    private String courseName;

    private Long nCount;

    private Long courseId;

    private List<RollCallOfClassDTO> classInfo;


    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getnCount() {
        return nCount;
    }

    public void setnCount(Long nCount) {
        this.nCount = nCount;
    }

    public List<RollCallOfClassDTO> getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(List<RollCallOfClassDTO> classInfo) {
        this.classInfo = classInfo;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
