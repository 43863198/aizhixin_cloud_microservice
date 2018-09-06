package com.aizhixin.cloud.orgmanager.classschedule.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractOnlyIdEntity;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 教学班学生关系
 * Created by zhen.pan on 2017/4/25.
 */
@Entity(name = "T_TEACHING_CLASS_STUDENTS")
@ToString
public class TeachingClassStudents extends AbstractOnlyIdEntity {
    /**
     * 教学班
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHING_CLASS_ID")
    @Getter @Setter private TeachingClass teachingClass;

    /**
     * 学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @Getter @Setter private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTER_ID")
    @Getter @Setter private Semester semester;

    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
}
