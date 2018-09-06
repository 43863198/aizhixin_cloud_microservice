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
@Entity(name = "dd_credit_report_record")
@ToString
public class CreditReportRecord extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "report_id")
    @Getter
    @Setter
    private Long reportId;

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

    @Column(name = "ques_id")
    @Getter
    @Setter
    private Long quesId;

    @Column(name = "avg_score")
    @Getter
    @Setter
    private Float avgScore;
}
