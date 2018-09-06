package com.aizhixin.cloud.orgmanager.training.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 实训组信息
 */
@Entity
@Table(name = "T_TRAINING_GROUP")
@ToString
public class TrainingGroup  extends AbstractEntity {

    /**
     * 实训组编码
     */
    @Column(name = "GROP_CODE")
    @Getter
    @Setter
    private String gropCode;

    /**
     * 实训组名称
     */
    @Column(name = "GROP_NAME")
    @Getter
    @Setter
    private String gropName;
    /**
     * 实训企业导师id
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CORPORATE_MENTORS_ID")
    @Getter
    @Setter
    private CorporateMentorsInfo corporateMentorsInfo;
    /**
     * 学校老师id
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHER_ID")
    @Getter
    @Setter
    private User teacher;
    /**
     * 学校id
     */
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long orgId;

    /**
     * 实训开始时间
     */
    @Column(name = "START_DATE")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date startDate;

    /**
     * 实训结束时间
     */
    @Column(name = "END_DATE")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date endDate;



}