package com.aizhixin.cloud.dd.questionnaire.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "DD_QUESTIONNAIRE_ASSGIN_USER")
public class QuestionnaireAssignUser extends AbstractOnlyIdAndCreatedDateEntity implements Serializable {

    /**
     * 问卷id
     */
    @Column(name = "ques_id")
    @Getter
    @Setter
    private Long quesId;

    /**
     * user id
     */
    @Column(name = "user_id")
    @Getter
    @Setter
    private Long userId;

    @Column(name = "user_name")
    @Getter
    @Setter
    private String userName;

    @Column(name = "job_num")
    @Getter
    @Setter
    private String jobNum;

    @Column(name = "college_id")
    @Getter
    @Setter
    private Long collegeId;

    @Column(name = "college_name")
    @Getter
    @Setter
    private String collegeName;

    /**
     * user type 70:学生 60:教师
     */
    @Column(name = "user_type")
    @Getter
    @Setter
    private Integer userType;

    /**
     * user type 10:非授课教师 20:授课教师
     */
    @Column(name = "teacher_type")
    @Getter
    @Setter
    private Integer teacherType;

    /**
     * 权重
     */
    @Column(name = "weight")
    @Getter
    @Setter
    private Long weight;

    /**
     * 提交时间
     */
    @Column(name = "commit_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date commitDate;
    /**
     * 评分
     */
    @Column(name = "score")
    @Getter
    @Setter
    private Integer score;
    /**
     * 状态
     */
    @Column(name = "status")
    @Getter
    @Setter
    private Integer status;
    /**
     * 评语
     */
    @Column(name = "comment")
    @Getter
    @Setter
    private String comment;

}
