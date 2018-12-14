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
@Entity(name = "dd_credit_commit_stu_record_log")
@ToString
public class CreditCommitStudentRecordLog extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "commit_stu_log_id")
    @Getter
    @Setter
    private Long commitStuLogId;

    @Column(name = "credit_id")
    @Getter
    @Setter
    private Long creditId;

    @Column(name = "stu_id")
    @Getter
    @Setter
    private Long stuId;

    @Column(name = "ques_id")
    @Getter
    @Setter
    private Long quesId;

    @Column(name = "scores")
    @Getter
    @Setter
    private String scores;

    @Column(name = "avg_score")
    @Getter
    @Setter
    private Float avgScore;
}
