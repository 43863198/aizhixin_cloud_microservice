package com.aizhixin.cloud.orgmanager.electrict.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 电子围栏设置信息
 */
@Entity
@Table(name = "T_ELECTRIC_FENCE_INFO")
@ToString
public class ElectricFenceInfo extends AbstractEntity {
    /**
     * 学校id
     */
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long organId;

    /**
     * 学期id
     */
    @Column(name = "SEMESTER_ID")
    @Getter
    @Setter
    private Long semesterId;

    /**
     * 启用或者关闭电子围栏(10启用，20关闭)
     */
    @Column(name = "SETUP_OR_CLOSE")
    @Getter
    @Setter
    private Integer setupOrClose;

    /**
     * 学校范围
     */
    @Column(name = "LLTUDES")
    @Getter
    @Setter
    private String lltudes;

    /**
     * 监控日期
     */
    @Column(name = "MONITOR_DATE")
    @Getter
    @Setter
    private String monitorDate;

    /**
     * 非监控日期
     */
    @Column(name = "NOMONITOR_DATE")
    @Getter
    @Setter
    private String nomonitorDate;

    /**
     * 申报时隔
     **/
    @Column(name = "NOTICE_TIME_INTERVAL")
    @Getter
    @Setter
    private Long noticeTimeInterval;
}