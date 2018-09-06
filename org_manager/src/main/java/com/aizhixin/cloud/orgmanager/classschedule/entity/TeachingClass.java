package com.aizhixin.cloud.orgmanager.classschedule.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 教学班
 * Created by zhen.pan on 2017/4/25.
 */

@Entity(name = "T_TEACHING_CLASS")
@ToString
public class TeachingClass extends AbstractEntity {
    @NotNull
    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTER_ID")
    @Getter @Setter private Semester semester;

//    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    @Getter @Setter private Course course;
    /**
     * 关联行政班还是直接关联学生
     */
    @Column(name = "CLASS_OR_STUDENTS")
    @Getter
    @Setter
    private Integer classOrStudents;

    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long orgId;
    /**-------------------------------以下部分为冗余字段------------------------------------**/
    @Column(name = "TEACHER_NAMES")
    @Getter
    @Setter
    private String teacherNames;

    @Column(name = "CLASSES_NAMES")
    @Getter
    @Setter
    private String classesNames;

    @Column(name = "STUDENTS_COUNT")
    @Getter
    @Setter
    private Long studentsCount;

    //	@NotNull
    @Column(name = "CODE")
    @Getter @Setter private String code;

    /**
     * 创建标志
     */
    @Column(name = "source")
    @Getter@Setter
    private Integer source;
}