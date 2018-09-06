package com.aizhixin.cloud.school.schoolinfo.entity;

import com.aizhixin.cloud.school.common.entity.AbstractStringEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "EXCELLENTCOURSEAPPLY")
@Data
public class ExcellentCourseApply extends AbstractStringEntity {
    /**
     * 教师id
     */
    @Column(name = "teacher_id")
    private Long teacherId;
    /**
     * 教师名
     */
    @Column(name = "teacher_name")
    private String teacherName;
    /**
     * 课程id
     */
    @Column(name = "course_id")
    private Long courseId;
    /**
     * 课程名
     */
    private String courseName;
    /**
     * 10：申请中 20：通过，30：拒绝
     */
    @Column(name = "state")
    private Integer state;
    /**
     * 学校id
     */
    @Column(name = "org_id")
    private Long orgId;
}
