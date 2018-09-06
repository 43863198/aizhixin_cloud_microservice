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
 * 电子围栏基础信息表
 *
 * @author HUM
 */
@Entity
@Table(name = "T_ELECTRIC_FENCE_BASE")
@ToString
public class ElectricFenceBase extends AbstractEntity {
    /**
     * 学校id
     */
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long organId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @Getter
    @Setter
    private User user;

    /**
     * 位置
     */
    @Column(name = "ADDRESS")
    @Getter
    @Setter
    private String address;

    /**
     * 设备码
     */
    @Column(name = "EQUIPMENT_CODE")
    @Getter
    @Setter
    private String equipmentCode;

    /**
     * 经纬度
     */
    @Column(name = "LLTUDE")
    @Getter
    @Setter
    private String lltude;

    /**
     * 申报时间
     */
    @Column(name = "NOTICE_TIME")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date noticeTime;

    /**
     * 是否超出范围   1:超出范围   0:在范围内
     */
    @Column(name = "OUT_OF_RANGE")
    @Getter
    @Setter
    private Integer outOfRange;

    /**
     * 离线时长
     */
    @Column(name = "LEAVE_NUM")
    @Getter
    @Setter
    private Long leaveNum;
    /**
     * 连线方式
     */
    @Column(name = "CONNECT_WAY")
    @Getter
    @Setter
    private String connectWay;
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
}
