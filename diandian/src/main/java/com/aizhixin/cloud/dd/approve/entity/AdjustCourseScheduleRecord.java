package com.aizhixin.cloud.dd.approve.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "DD_ADJUST_COURSE_SCHEDULE_RECORD")
@Data
public class AdjustCourseScheduleRecord extends AbstractEntity {
    private static final long serialVersionUID = -2833890921053212135L;
    /**
     * 调课类型
     */
    @Column(name = "type")
    private String type;
    /**
     * 课程名称
     */
    @Column(name = "course_name")
    private String courseName;

    /**
     * 教学班id
     */
    @Column(name = "teaching_class_id")
    private Long teachingClassId;

    /**
     * 教学班名称
     */
    @Column(name = "teaching_class_name")
    private String teachingClassName;

    /**
     * 教师名称
     */
    @Column(name = "teacher_name")
    private String teacherName;
    /**
     * 原始上课时间
     */
    @Column(name = "ago_attend_class_time")
    private String agoAttendClassTime;
    /**
     * 原始上课地址
     */
    @Column(name = "ago_attend_class_address")
    private String agoAttendClassAddress;
    /**
     * 新的上课时间
     */
    @Column(name = "new_attend_class_time")
    private String newAttendClassTime;
    /**
     * 新的上课地址
     */
    @Column(name = "new_attend_class_address")
    private String newAttendClassAddress;
    /**
     * 教师id或学生id
     */
    @Column(name = "user_id")
    private Long userId;
}
