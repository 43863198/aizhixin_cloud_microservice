package com.aizhixin.cloud.orgmanager.classschedule.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractOnlyIdEntity;
import com.aizhixin.cloud.orgmanager.company.entity.Period;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 教学班的课表
 * Created by zhen.pan on 2017/4/25.
 */

@Entity(name = "T_SCHOOL_TIME_TABLE")
@ToString
public class SchoolTimeTable extends AbstractOnlyIdEntity {
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
     * 起始周
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "START_WEEK_ID")
    @Getter @Setter private Week startWeek;
    /**
     * 结束周
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "END_WEEK_ID")
    @Getter @Setter private Week endWeek;
    /**
     * 起始周数字，第几周
     */
    @Column(name = "START_WEEK_NO")
    @Getter @Setter private Integer startWeekNo;
    /**
     * 结束周数字，第几周
     */
    @Column(name = "END_WEEK_NO")
    @Getter @Setter private Integer endWeekNo;
    /**
     * 单周或双周,10不区分单双周,20单周,30双周
     */
    @Column(name = "SINGLE_OR_DOUBLE")
    @Getter @Setter private Integer singleOrDouble;
    /**
     * 星期几（周日1，周六7）
     */
    @Column(name = "DAY_OF_WEEK")
    @Getter @Setter private Integer dayOfWeek;
    /**
     * 第几节
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERIOD_ID")
    @Getter @Setter private Period period;
    /**
     * 第几节
     */
    @Column(name = "PERIOD_NO")
    @Getter @Setter private Integer periodNo;
    /**
     * 本堂课持续节
     */
    @Column(name = "PERIOD_NUM")
    @Getter @Setter private Integer periodNum;
    /**
     * 教室
     */
    @Column(name = "CLASS_ROOM")
    @Getter @Setter private String classroom;
    /**
     * 备注
     */
    @Column(name = "REMARK")
    @Getter @Setter private String remark;
    /**
     * 颜色
     */
    @Column(name = "COLOR")
    @Getter @Setter private String color;
}