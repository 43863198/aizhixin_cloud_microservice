package com.aizhixin.cloud.dd.rollcall.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity
@Table(name = "DD_LEAVE")
public class Leave extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "`org_id`")
    @Getter
    @Setter
    private Long orgId;

    // 请假学生id
    @Column(name = "`student_id`")
    @Getter
    @Setter
    private Long studentId;

    @Column(name = "`student_name`")
    @Getter
    @Setter
    private String studentName;

    @Column(name = "`student_job_num`")
    @Getter
    @Setter
    private String studentJobNum;

    @Column(name = "`class_name`")
    @Getter
    @Setter
    private String className;

    // 审批老师ID
    @Column(name = "`head_teacher_id`")
    @Getter
    @Setter
    private Long headTeacherId;

    @Column(name = "teacher_name")
    @Getter
    @Setter
    private String teacherName;

    @Column(name = "teacher_job_num")
    @Getter
    @Setter
    private String teacherJobNum;

    // 请假类型 课程节/天
    @Column(name = "`request_type`")
    @Getter
    @Setter
    private String requestType;
    // 请假开始时间 (如果请假类型为节 就是请假当天)
    @Column(name = "`start_date`")
    @Getter
    @Setter
    private Date startDate;
    @Column(name = "`start_time`")
    @Getter
    @Setter
    private Date startTime;
    // 请假结束时间 (如果请假类型为节 为空)
    @Column(name = "`end_date`")
    @Getter
    @Setter
    private Date endDate;
    @Column(name = "`end_time`")
    @Getter
    @Setter
    private Date endTime;
    // 请假理由
    @Column(name = "`request_content`")
    @Getter
    @Setter
    private String requestContent;
    // 驳回理由
    @Column(name = "`reject_content`")
    @Getter
    @Setter
    private String rejectContent;
    // 状态 申请/驳回/通过/删除
    @Column(name = "`status`")
    @Getter
    @Setter
    private String status;
    // 是否离校
    @Column(name = "`leave_school`")
    @Getter
    @Setter
    private Boolean leaveSchool;

    // 开始课程节ID
    @Column(name = "`start_period_id`")
    @Getter
    @Setter
    private Long startPeriodId;

    // 结束课程节ID
    @Column(name = "`end_period_id`")
    @Getter
    @Setter
    private Long endPeriodId;

    @Column(name = "`leave_picture_urls`")
    @Getter
    @Setter
    private String leavePictureUrls;

    @Column(name = "`leave_public`")
    @Getter
    @Setter
    private Integer leavePublic;

    @Column(name = "`leave_type`")
    @Getter
    @Setter
    private Integer leaveType;

    @Column(name = "`duration`")
    @Getter
    @Setter
    private String duration;
}
