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
@Entity(name = "dd_credit_report")
@ToString
public class CreditReport extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    @Column(name = "org_id")
    @Getter
    @Setter
    private Long orgId;

    @Column(name = "credit_id")
    @Getter
    @Setter
    private Long creditId;

    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId;

    @Column(name = "teacher_name")
    @Getter
    @Setter
    private String teacherName;

    @Column(name = "templet_id")
    @Getter
    @Setter
    private Long templetId;

    @Column(name = "templet_name")
    @Getter
    @Setter
    private String templetName;

    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId;

    @Column(name = "class_name")
    @Getter
    @Setter
    private String className;

    @Column(name = "commit_count")
    @Getter
    @Setter
    private Integer commitCount;

    @Column(name = "avg_score")
    @Getter
    @Setter
    private Float avgScore;
}
