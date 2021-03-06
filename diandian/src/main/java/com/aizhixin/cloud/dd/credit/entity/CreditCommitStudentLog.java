package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author hsh
 */
@Entity(name = "dd_credit_commit_student_log")
@ToString
public class CreditCommitStudentLog extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "commit_log_id")
    @Getter
    @Setter
    private Long commitLogId;

    @Column(name = "credit_id")
    @Getter
    @Setter
    private Long creditId;

    @Column(name = "stu_id")
    @Getter
    @Setter
    private Long stuId;

    @Column(name = "stu_name")
    @Getter
    @Setter
    private String stuName;

    @Column(name = "job_num")
    @Getter
    @Setter
    private String jobNum;

    @Column(name = "avatar")
    @Getter
    @Setter
    private String avatar;

    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId;

    @Column(name = "avg_score")
    @Getter
    @Setter
    private Float avgScore;

    @Column(name = "avg_score_pct")
    @Getter
    @Setter
    private Float avgScorePct;

    @Column(name = "rating_count")
    @Getter
    @Setter
    private Integer ratingCount;
}
