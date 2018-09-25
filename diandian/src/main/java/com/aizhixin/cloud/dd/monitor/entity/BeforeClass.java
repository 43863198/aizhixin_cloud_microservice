package com.aizhixin.cloud.dd.monitor.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Document(collection = "BeforeClass")
@Data
public class BeforeClass extends Base {
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
    protected String status;

    public BeforeClass() {}

    public BeforeClass(Integer successFlag, String message, Long useTime, Date date, Long orgId, String orgName, Long scheduleId, String teachDate, Long teachingclassId,
        String teachingclassName, Long courseId, String courseName, Long teacherId, String teacherName, Integer periodNo, Integer perioidNum, String startTime, String endTime,
        String status) {
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
        this.status = status;
    }
}
