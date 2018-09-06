package com.aizhixin.cloud.orgmanager.classschedule.entity;


import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 学校学期时间内统一休假日历记录信息
 */
@Entity(name = "T_SCHOOL_HOLIDAY")
@ToString
@NoArgsConstructor
public class SchoolHoliday extends AbstractEntity {
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
     * 假期名称
     */
    @NotNull
    @Column(name = "name")
    @Getter @Setter private String name;
    /**
     * 放假开始日期
     */
    @Column(name = "START_DATE")
    @Getter @Setter private String startDate;
    /**
     * 放假结束日期
     */
    @Column(name = "END_DATE")
    @Getter @Setter private String endDate;
    /**
     * 放假开始第几周
     */
    @NotNull
    @Column(name = "START_WEEK_NO")
    @Getter @Setter private Integer startWeekNo;
    /**
     * 放假开始星期几（周日1，周六7）
     */
    @NotNull
    @Column(name = "START_DAY_OF_WEEK")
    @Getter @Setter private Integer startDayOfWeek;
    /**
     * 放假结束第几周
     */
    @NotNull
    @Column(name = "END_WEEK_NO")
    @Getter @Setter private Integer endWeekNo;
    /**
     * 放假结束星期几（周日1，周六7）
     */
    @NotNull
    @Column(name = "END_DAY_OF_WEEK")
    @Getter @Setter private Integer endDayOfWeek;
}
