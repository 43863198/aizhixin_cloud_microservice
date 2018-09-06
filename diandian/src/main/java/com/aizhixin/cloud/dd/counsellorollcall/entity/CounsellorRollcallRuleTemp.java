package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "DD_COUNSELLORROLLCALL_RULE_TEMP")
@ToString
public class CounsellorRollcallRuleTemp extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "rule_id")
    @Getter
    @Setter
    private Long ruleId;

    @Column(name = "user_id")
    @Getter
    @Setter
    private Long userId;

    //开始时间
    @Column(name = "start_time")
    @Getter
    @Setter
    private String startTime;

    //开始弹性时间
    @Column(name = "start_flex_time")
    @Getter
    @Setter
    private Integer startFlexTime;

    //迟到时间
    @Column(name = "late_time")
    @Getter
    @Setter
    private Integer lateTime;

    //结束时间
    @Column(name = "end_time")
    @Getter
    @Setter
    private String endTime;

    //结束弹性时间
    @Column(name = "end_flex_time")
    @Getter
    @Setter
    private Integer endFlexTime;

    //结束签到时间
    @Column(name = "stop_time")
    @Getter
    @Setter
    private Integer stopTime;

    //签到日期（周几，逗号隔开）
    @Column(name = "days")
    @Getter
    @Setter
    private String days;
}
