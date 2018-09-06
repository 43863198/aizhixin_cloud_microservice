/**
 *
 */
package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.*;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "DD_ASSESS")
public class Assess extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = -991843439808239680L;

    @Column(name = "schedule_id")
    @Getter
    @Setter
    private Long scheduleId; // 课表

    @Column(name = "student_id")
    @Getter
    @Setter
    private Long studentId; // 学生

    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId; // 教师

    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId; // 班级

    @Column(name = "semester_id")
    @Getter
    @Setter
    private Long semesterId; // 学期

    @Column(name = "course_id")
    @Getter
    @Setter
    private Long courseId; // 课程

    @Column(name = "content")
    @Getter
    @Setter
    private String content; // 评教内容

    @Column(name = "score")
    @Getter
    @Setter
    private Integer score;
    
    //学生名称
    @Column(name="stu_name")
    @Getter@Setter          
    private String stuName;
    
    //教师名称
    @Column(name="teacher_name")
    @Getter@Setter
    private String teacherName;
    //是否匿名评价
    @Column(name="anonymity")
    @Setter@Getter
    private boolean anonymity;
    //模块
    @Column(name="module")
    @Getter@Setter
    private String module ;
    
    //回复数量
    @Column(name="revert_total")
    @Getter@Setter
    private Integer revertTotal=0;
    
    //评论者id
    @Column(name="comment_id")
    @Getter@Setter
    private Long commentId;
    
    //评论者名称
    @Column(name="comment_name")
    @Getter@Setter
    private String commentName;
    
    //评论来源id
    @Column(name="sourse_id")
    @Getter@Setter
    private Long sourseId;
}
