package com.aizhixin.cloud.dd.rollcall.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity
@Table(name = "DD_STUDENT_LEAVE_SCHEDULE")
public class StudentLeaveSchedule extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 8522382447977658100L;
    // 请假学生id
    @Column(name = "`STUDENT_ID`")
    @Getter
    @Setter
    private Long studentId;

    // 上课老师ID
    @Column(name = "`teacher_id`")
    @Getter
    @Setter
    private Long teacherId;

    // 课程
    @Column(name = "`course_id`")
    @Getter
    @Setter
    private Long courseId;

    // 请假
    @Column(name = "`reques_date`")
    @Getter
    @Setter
    private Date requesDate;

    // 课程节
    @Column(name = "`request_period_id`")
    @Getter
    @Setter
    private Long requestPeriodId;

    // 排课表ID

    @JoinColumn(name = "`SCHEDULE_ID`")
    @Getter
    @Setter
    private Long scheduleId;

    // 请假表id
    @JoinColumn(name = "LEAVE_ID")
    @Getter
    @Setter
    private Long leaveId;

}
