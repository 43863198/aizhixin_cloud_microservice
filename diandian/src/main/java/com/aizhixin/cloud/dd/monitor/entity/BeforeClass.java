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
    Long orgId;
    @Indexed
    String orgName;
    @Indexed
    Long scheduleId;
    @Indexed
    String teachDate;
    Long teachingclassId;
    String teachingclassName;
    Long courseId;
    String courseName;
    Long teacherId;
    String teacherName;
    Integer periodNo;
    Integer perioidNum;
    String startTime;
    String endTime;
    String status;

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
