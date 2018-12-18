package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hsh
 */
@Entity(name = "dd_credit_commit_log")
@ToString
public class CreditCommitLog implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Getter
    @Setter
    protected Long id;

    @Column(name = "report_id")
    @Getter
    @Setter
    private Long reportId;

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

    @CreatedDate
    @Column(name = "CREATED_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    protected Date createdDate = new Date();

    @Column(name = "DELETE_FLAG")
    @Getter
    @Setter
    protected Integer deleteFlag = DataValidity.VALID.getState();
}
