package com.aizhixin.cloud.orgmanager.classschedule.entity;


import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 临时课表
 */
@Entity(name = "T_TEMP_COURSE_SCHEDULE")
@ToString
@NoArgsConstructor
public class TempCourseSchedule extends AbstractEntity {
    /**
     * 学校
     */
    @NotNull
    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
    /**
     * 学期
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTER_ID")
    @Getter @Setter private Semester semester;
    /**
     * 教学班
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHING_CLASS_ID")
    @Getter @Setter private TeachingClass teachingClass;

    /**
     * 调课类型（10增加、20停止）
     */
    @NotNull
    @Column(name = "ADJUST_TYPE")
    @Getter @Setter private Integer adjustType;

    /**
     * 调停课关联数据
     */
    @Column(name = "ADJUST_ID")
    @Getter @Setter private Long adjustId;

    /**
     * 第几周
     */
    @NotNull
    @Column(name = "WEEK_NO")
    @Getter @Setter private Integer weekNo;

    /**
     * 星期几（周日1，周六7）
     */
    @NotNull
    @Column(name = "DAY_OF_WEEK")
    @Getter @Setter private Integer dayOfWeek;
    /**
     * 停（加）课日期
     */
    @Column(name = "EVENT_DATE")
    @Getter @Setter private String eventDate;

    /**
     * 第几节
     */
    @NotNull
    @Column(name = "PERIOD_NO")
    @Getter @Setter private Integer periodNo;

    /**
     * 持续节
     */
    @NotNull
    @Column(name = "PERIOD_NUM")
    @Getter @Setter private Integer periodNum;

    /**
     * 教室
     */
    @Column(name = "CLASSROOM")
    @Getter @Setter private String classroom;
}
