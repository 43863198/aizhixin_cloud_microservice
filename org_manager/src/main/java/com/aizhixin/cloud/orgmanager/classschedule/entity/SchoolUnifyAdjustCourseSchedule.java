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
 * 学校学期时间内统一调整某一天课表到另外一天(如果目标的那一天也有课程排课数据，会覆盖处理)
 */
@Entity(name = "T_SCHOOL_UNIFY_ADJUST_COURSE_SCHEDULE")
@ToString
@NoArgsConstructor
public class SchoolUnifyAdjustCourseSchedule extends AbstractEntity {
    /**
     * 学校
     */
    @NotNull
    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
    /**
     * 调课说明
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTER_ID")
    @Getter @Setter private Semester semester;
    /**
     * 假期名称
     */
    @NotNull
    @Column(name = "name")
    @Getter @Setter private String name;
    /**
     * 原课表日期
     */
    @Column(name = "SRC_DATE")
    @Getter @Setter private String srcDate;
    /**
     * 目标课表日期
     */
    @Column(name = "DEST_DATE")
    @Getter @Setter private String destDate;
    /**
     * 原课表第几周
     */
    @NotNull
    @Column(name = "SRC_WEEK_NO")
    @Getter @Setter private Integer srcWeekNo;
    /**
     * 原课表星期几（周日1，周六7）
     */
    @NotNull
    @Column(name = "SRC_DAY_OF_WEEK")
    @Getter @Setter private Integer srcDayOfWeek;
    /**
     * 目标课表第几周
     */
    @NotNull
    @Column(name = "DEST_WEEK_NO")
    @Getter @Setter private Integer destWeekNo;
    /**
     * 目标课表星期几（周日1，周六7）
     */
    @NotNull
    @Column(name = "DEST_DAY_OF_WEEK")
    @Getter @Setter private Integer destDayOfWeek;
}