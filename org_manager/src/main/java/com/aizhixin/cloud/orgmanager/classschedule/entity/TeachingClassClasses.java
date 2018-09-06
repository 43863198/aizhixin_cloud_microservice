package com.aizhixin.cloud.orgmanager.classschedule.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractOnlyIdEntity;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 教学班、行政班关系
 * Created by zhen.pan on 2017/4/25.
 */
@Entity(name = "T_TEACHING_CLASS_CLASSES")
@ToString
public class TeachingClassClasses extends AbstractOnlyIdEntity {
    /**
     * 教学班
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHING_CLASS_ID")
    @Getter @Setter private TeachingClass teachingClass;

    /**
     * 班级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASSES_ID")
    @Getter @Setter private Classes classes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTER_ID")
    @Getter @Setter private Semester semester;

    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
}
