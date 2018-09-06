package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by LIMH on 2017/11/29.
 * <p>
 * 定时
 */
@Entity(name = "DD_ALARMCLOCK")
@ToString
public class AlarmClock extends AbstractEntity {

    @NotNull
    @OneToOne
    @JoinColumn(name = "TEMPGROUP_ID")
    @Getter
    @Setter
    private TempGroup tempGroup;

    @Column(name = "STATUS")
    @Getter
    @Setter
    private Boolean status = Boolean.TRUE;

    @NotNull
    @Column(name = "CLOCK_TIME")
    @Getter
    @Setter
    private String clockTime;

    @Column(name = "late_time")
    @Getter
    @Setter
    private String lateTime;

    @Column(name = "second_time")
    @Getter
    @Setter
    private String secondTime;

    @Column(name = "end_time")
    @Getter
    @Setter
    private String endTime;

    @Column(name = "start_end_time")
    @Getter
    @Setter
    private String startEndTime;

    @NotNull
    @Column(name = "CLOCK_MODE")
    @Getter
    @Setter
    private String clockMode;
}
