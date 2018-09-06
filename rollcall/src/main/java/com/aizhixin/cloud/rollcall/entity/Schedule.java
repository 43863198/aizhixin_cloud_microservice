package com.aizhixin.cloud.rollcall.entity;

import com.aizhixin.cloud.rollcall.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 排课信息
 *
 * @author meihua.li
 */
@Entity(name = "dd_schedule")
@ToString
public class Schedule extends AbstractEntity {
    private static final long serialVersionUID = -4138514236088577990L;


    @Column(name = "organ_id")
    @Getter
    @Setter
    private Long organId;

    @NotNull
    @Column(name = "course_id")
    @Getter
    @Setter
    private Long courseId;

    @Column(name = "course_name")
    @Getter
    @Setter
    private String courseName;

    @NotNull
    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId;

    @Column(name = "teacher_name")
    @Getter
    @Setter
    private String teacherNname;

    @NotNull
    @Column(name = "semester_id")
    @Getter
    @Setter
    private Long semesterId;

    @Column(name = "semester_name")
    @Getter
    @Setter
    private String semesterName;

    @NotNull
    @Column(name = "week_id")
    @Getter
    @Setter
    private Long weekId;

    @Column(name = "week_name")
    @Getter
    @Setter
    private String weekName;

    @NotNull
    @Column(name = "day_of_week")
    @Getter
    @Setter
    private Integer dayOfWeek;

    @NotNull
    @Column(name = "period_id")
    @Getter
    @Setter
    private Long periodId;

    @Column(name = "period_no")
    @Getter
    @Setter
    private Integer periodNo;

    @Column(name = "period_num")
    @Getter
    @Setter
    private Integer periodNum;

    @NotNull
    @Column(name = "start_time")
    @Getter
    @Setter
    private String scheduleStartTime;

    @NotNull
    @Column(name = "end_time")
    @Getter
    @Setter
    private String scheduleEndTime;

    @Column(name = "classroom_Name")
    @Getter
    @Setter
    private String classRoomName;

    @NotNull
    @Column(name = "teach_date")
    @Getter
    @Setter
    private String teachDate;

    @NotNull
    @Column(name = "teachingclass_id")
    @Getter
    @Setter
    private Long teachingclassId;

    @Column(name = "teachingclass_name")
    @Getter
    @Setter
    private String teachingclassName;

    @Column(name = "teachingclass_code")
    @Getter
    @Setter
    private String teachingclassCode;

    @Column(name = "is_init_rollcall")
    @Getter
    @Setter
    private Boolean isInitRollcall;



}