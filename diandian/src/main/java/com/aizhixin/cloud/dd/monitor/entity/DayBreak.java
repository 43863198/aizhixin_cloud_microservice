package com.aizhixin.cloud.dd.monitor.entity;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Document(collection = "DayBreak")
@Data
public class DayBreak extends Base {
    @Id
    protected String id;
    @Indexed
    protected Long orgId;
    @Indexed
    protected String orgName;
    @Indexed
    protected Long scheduleId;
    @Indexed
    protected String teachDate;
    protected Long teachingclassId;
    protected String teachingclassName;
    protected Long courseId;
    protected String courseName;
    protected Long teacherId;
    protected String teacherName;
    protected Integer periodNo;
    protected Integer perioidNum;
    protected String startTime;
    protected String endTime;
    protected Boolean compareflag;

    public DayBreak() {
    }

    public DayBreak(Integer successFlag, String message, Long useTime, Date date) {
        super(successFlag, message, useTime, date);
    }

    public DayBreak(Long orgId, String orgName, Long scheduleId, String teachDate, Long teachingclassId, String teachingclassName, Long courseId, String courseName, Long teacherId,
                    String teacherName, Integer periodNo, Integer perioidNum, String startTime, String endTime, Integer successFlag, String message, Long useTime, Date date) {
        super(successFlag, message, useTime, date);
        this.orgId = orgId;
        this.orgName = orgName;
        this.scheduleId = scheduleId;
        this.teachDate = teachDate;
        this.teachingclassId = teachingclassId;
        this.teachingclassName = teachingclassName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.periodNo = periodNo;
        this.perioidNum = perioidNum;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayBreak(Long orgId, String orgName, Long scheduleId, String teachDate, Long teachingclassId, String teachingclassName, Long courseId, String courseName, Long teacherId,
                    String teacherName, Integer periodNo, Integer perioidNum, String startTime, String endTime, Boolean compareflag, Integer successFlag, String message, Long useTime,
                    Date date) {
        super(successFlag, message, useTime, date);
        this.orgId = orgId;
        this.orgName = orgName;
        this.scheduleId = scheduleId;
        this.teachDate = teachDate;
        this.teachingclassId = teachingclassId;
        this.teachingclassName = teachingclassName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.periodNo = periodNo;
        this.perioidNum = perioidNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.compareflag = compareflag;
    }
}
