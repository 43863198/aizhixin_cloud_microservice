package com.aizhixin.cloud.rollcall.entity;


import com.aizhixin.cloud.rollcall.common.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "DD_ROLLCALL")
@ToString
public class RollCall extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /*
     * 学生上课表id
     */
    @NotNull
    @Column(name = "schedule_Rollcall_id")
    @Getter
    @Setter
    private Long scheduleRollcallId;

    /*
     * 课程id
     */
    @Column(name = "course_id")
    @Getter
    @Setter
    private Long courseId;

    /*
     * 学期id
     */
    @Column(name = "semester_id")
    @Getter
    @Setter
    private Long semesterId;

    /*
     * 班级id
     */
    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId;

    /*
 * 班级id
 */
    @Column(name = "class_name")
    @Getter
    @Setter
    private String className;
    /*
     * 学生id
     */
    @NotNull
    @Column(name = "student_id")
    @Getter
    @Setter
    private Long studentId;
    /*
       * 学生学号
       */
    @Column(name = "student_num")
    @Getter
    @Setter
    private String studentNum = "";
    /*
     * 老师id
     */
    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId;

    /*
     * 教学班id
     */
    @Column(name = "TEACHINGCLASS_ID")
    @Getter
    @Setter
    private Long teachingClassId;
    /*
     * 点名结果
     */
    @Column(name = "type")
    @Getter
    @Setter
    private String type;

    /*
     * 是否可以点名
     */
    @Column(name = "can_roll_call")
    @Getter
    @Setter
    private Boolean canRollCall = false;

    /*
     * 上次点名结果
     */
    @Column(name = "last_type")
    @Getter
    @Setter
    private String lastType;

    /*
     * 本轮自动点名是否签到过
     */
    @Column(name = "HAVE_REPORT")
    @Getter
    @Setter
    private Boolean haveReport = false;

    /**
     * 经纬度信息
     */
    @Column(name = "GPS_LOCALTION")
    @Getter
    @Setter
    private String gpsLocation;

    /**
     * gps详细位置信息
     */
    @Column(name = "GPS_DETAIL")
    @Getter
    @Setter
    private String gpsDetail;

    /**
     * gps 类型 (wifi,4g,gps)
     */
    @Column(name = "GPS_TYPE")
    @Getter
    @Setter
    private String gpsType;

    @Column(name = "DISTANCE")
    @Getter
    @Setter
    private String distance;

    @Column(name = "SIGN_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter
    @Setter
    private Date signTime;

    @Column(name = "DEVICETOKEN")
    @Getter
    @Setter
    private String deviceToken;

    @Column(name = "student_name")
    @Getter
    @Setter
    private String studentName;


    @Column(name = "professional_id")
    @Getter
    @Setter
    private Long professionalId;

    @Column(name = "professional_name")
    @Getter
    @Setter
    private String professionalName;

    @Column(name = "college_id")
    @Getter
    @Setter
    private Long collegeId;

    @Column(name = "college_name")
    @Getter
    @Setter
    private String collegeName;

    @Column(name = "teaching_year")
    @Getter
    @Setter
    private String teachingYear;
}
