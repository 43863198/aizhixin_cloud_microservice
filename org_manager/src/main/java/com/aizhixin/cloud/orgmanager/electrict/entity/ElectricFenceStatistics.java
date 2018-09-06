package com.aizhixin.cloud.orgmanager.electrict.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Entity
@Table(name = "T_ELECTRIC_FENCE_STATISTICS")
@ToString
public class ElectricFenceStatistics extends AbstractEntity {
    /**
     * 学校id
     */
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long organId;
    /**
     * 用户id
     */
    @Column(name = "USER_ID")
    @Getter
    @Setter
    private Long userId;
    /**
     * 用户名称
     */
    @Column(name = "USER_NAME")
    @Getter
    @Setter
    private String userName;
    /**
     * 学号/工号
     */
    @Column(name = "JOB_NUMBER")
    @Getter
    @Setter
    private String jobNumber;
    /**
     * 班级id
     */
    @Column(name = "CLASSES_ID")
    @Getter
    @Setter
    private Long classesId;
    /**
     * 班级名称
     */
    @Column(name = "CLASSES_NAME")
    @Getter
    @Setter
    private String classesName;
    /**
     * 专业id
     */
    @Column(name = "PROFESSIONAL_ID")
    @Getter
    @Setter
    private Long professionalId;
    /**
     * 专业名称
     */
    @Column(name = "PROFESSIONAL_NAME")
    @Getter
    @Setter
    private String professionalName;
    /**
     * 学院id
     */
    @Column(name = "COLLEGE_ID")
    @Getter
    @Setter
    private Long collegeId;
    /**
     * 学院名称
     */
    @Column(name = "COLLEGE_NAME")
    @Getter
    @Setter
    private String collegeName;
    /**
     *当天被检测到的次数
     */
    @Column(name = "CHECK_COUNT")
    @Getter
    @Setter
    private Integer checkCount;
    /**
     *当天超出范围的次数
     */
    @Column(name = "OUT_COUNT")
    @Getter
    @Setter
    private Integer outCount;
    /**
     *半小时检测到的次数
     */
    @Column(name = "SEMIH_COUNT")
    @Getter
    @Setter
    private Integer semihCount;
    /**
     *半小时超出范围的次数
     */
    @Column(name = "SEMIH_OUT_COUNT")
    @Getter
    @Setter
    private Integer semihOutCount;
    /**
     *激活
     */
    @Column(name = "FENCE_ACTIVATION")
    @Getter
    @Setter
    private Integer activation;
}
