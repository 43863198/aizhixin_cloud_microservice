package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

/**
 * 排课点名信息
 *
 * @author meihua.li
 */
@Entity(name = "dd_schedule_rollcall")
@ToString
public class ScheduleRollCall extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "SCHEDULE_ID")
    @Getter
    @Setter
    private Schedule schedule;

    @Column(name = "IS_OPEN_ROLL_CALL")
    @Getter
    @Setter
    private Boolean isOpenRollcall;

    @Column(name = "ROLL_CALL_TYPE")
    @Getter
    @Setter
    private String rollCallType;

    @Column(name = "LOCALTION")
    @Getter
    @Setter
    private String localtion;

    @Column(name = "COURSE_LATER_TIME")
    @Getter
    @Setter
    private Integer courseLaterTime;

    @Column(name = "absenteeism_time")
    @Getter
    @Setter
    private Integer absenteeismTime;

    @Column(name = "CLASSROOM_ROLLCALL")
    @Getter
    @Setter
    private Integer classRoomRollCall = 10;

    @Column(name = "IS_IN_CLASSROOM")
    @Getter
    @Setter
    private Boolean isInClassroom;

    @Column(name = "ATTENDANCE")
    @Getter
    @Setter
    private String attendance;
}