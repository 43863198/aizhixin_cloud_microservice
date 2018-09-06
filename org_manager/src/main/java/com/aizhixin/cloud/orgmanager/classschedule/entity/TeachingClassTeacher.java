package com.aizhixin.cloud.orgmanager.classschedule.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractOnlyIdEntity;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 教学班老师关系(某学期，老师带什么课，给那些班、那些学生代课)
 * Created by zhen.pan on 2017/4/25.
 */
@Entity(name = "T_TEACHING_CLASS_TEACHER")
@ToString
public class TeachingClassTeacher extends AbstractOnlyIdEntity {
    /**
     * 教学班
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHING_CLASS_ID")
    @Getter @Setter private TeachingClass teachingClass;

    /**
     * 教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHER_ID")
    @Getter @Setter private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTER_ID")
    @Getter @Setter private Semester semester;

    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
}
