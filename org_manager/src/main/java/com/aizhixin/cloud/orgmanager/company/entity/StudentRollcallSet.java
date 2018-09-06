package com.aizhixin.cloud.orgmanager.company.entity;


import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 学生考勤设置和取消考勤记录
 */
@Entity(name = "T_STUDENT_ROLLCALL_SET")
@ToString
public class StudentRollcallSet extends AbstractEntity {
    /**
     * 操作类型 10暂停考勤，20恢复考勤
     */
    @Column(name = "OPT")
    @Getter @Setter private Integer opt;
    /**
     * 原因说明
     */
    @Column(name = "MSG")
    @Getter @Setter private String msg;
    /**
     *  对那个学生生效
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @Getter @Setter private User student;
    /**
     * 操作人
     */
    @Column(name = "OPERATOR")
    @Getter @Setter private String operator;
    /**
     * 学生姓名
     */
    @Column(name = "STU_NAME")
    @Getter @Setter private String stuName;
    /**
     * 学生学号
     */
    @Column(name = "STU_JOB_NUMBER")
    @Getter @Setter private String stuJobNumber;
    /**
     * 学生班级
     */
    @Column(name = "STU_CLASSES_NAME")
    @Getter @Setter private String stuClassesName;
    /**
     * 学生年级
     */
    @Column(name = "STU_CLASSES_YEAR")
    @Getter @Setter private String stuClassesYear;
    /**
     * 学生专业名称
     */
    @Column(name = "STU_PROFESSIONAL_NAME")
    @Getter @Setter private String stuProfessionalName;
    /**
     * 学生学院名称
     */
    @Column(name = "STU_COLLEGE_NAME")
    @Getter @Setter private String stuCollegeName;
    /**
     * 组织、学校
     */
    @NotNull
    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
}