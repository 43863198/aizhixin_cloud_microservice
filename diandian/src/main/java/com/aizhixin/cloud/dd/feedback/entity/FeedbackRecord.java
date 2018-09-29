package com.aizhixin.cloud.dd.feedback.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hsh
 */
@Entity(name = "DD_FEEDBACK_RECORD")
@ToString
public class FeedbackRecord extends AbstractEntity {

    @Column(name = "org_id")
    @Getter
    @Setter
    private Long orgId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "templet_id")
    @Getter
    @Setter
    private FeedbackTemplet templet;

    //教学反馈 督导反馈
    @Column(name = "record_type")
    @Getter
    @Setter
    private Integer type;

    //教学班id
    @Column(name = "teaching_class_id")
    @Getter
    @Setter
    private String teachingClassId;

    //选课编号
    @Column(name = "teaching_class_code")
    @Getter
    @Setter
    private String teachingClassCode;

    //教学班名称
    @Column(name = "teaching_class_name")
    @Getter
    @Setter
    private String teachingClassName;

    //授课教师
    @Column(name = "teaching_class_teacher")
    @Getter
    @Setter
    private String teachingClassTeacher;

    //授课教师
    @Column(name = "teacher_job_num")
    @Getter
    @Setter
    private String teacherJobNum;

    //课程id
    @Column(name = "course_id")
    @Getter
    @Setter
    private String courseId;

    //课程名称
    @Column(name = "course_name")
    @Getter
    @Setter
    private String courseName;

    @Column(name = "class_names")
    @Getter
    @Setter
    private String classNames;

    //反馈者工号
    @Column(name = "job_num")
    @Getter
    @Setter
    private String jobNum;

    //反馈者名字
    @Column(name = "user_name")
    @Getter
    @Setter
    private String userName;

    //反馈者头像
    @Column(name = "user_avatar")
    @Getter
    @Setter
    private String userAvatar;

    //教师评价总分
    @Column(name = "teaching_score")
    @Getter
    @Setter
    private Float teachingScore;

    //学风评价总分
    @Column(name = "study_style_score")
    @Getter
    @Setter
    private Float studyStyleScore;

    public FeedbackRecord() {
    }
}
