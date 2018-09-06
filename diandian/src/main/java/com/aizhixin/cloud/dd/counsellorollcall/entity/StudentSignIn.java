package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by LIMH on 2017/11/29.
 * <p>
 * 学生签到表
 */
@Entity(name = "DD_STUDENTSIGNIN")
@ToString
public class StudentSignIn extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNSERLLORROLLCALL_ID")
    @Getter
    @Setter
    private CounsellorRollcall counsellorRollcall;

    @NotNull
    @Column(name = "STUDENT_ID")
    @Getter
    @Setter
    private Long studentId;

    @NotNull
    @Column(name = "STUDENT_NAME")
    @Getter
    @Setter
    private String studentName;

    @NotNull
    @Column(name = "STUDENT_NUM")
    @Getter
    @Setter
    private String studentNum;

    @Column(name = "GPS_LOCATION")
    @Getter
    @Setter
    private String gpsLocation;

    @Column(name = "GPS_DETAIL")
    @Getter
    @Setter
    private String gpsDetail;

    @Column(name = "GPS_TYPE")
    @Getter
    @Setter
    private String gpsType;

    @Column(name = "SIGN_TIME")
    @Getter
    @Setter
    private Timestamp signTime;

    @Column(name = "DEVICE_TOKEN")
    @Getter
    @Setter
    private String deviceToken;

    @Column(name = "HAVE_REPORT")
    @Getter
    @Setter
    private Integer haveReport = 0;

    @Column(name = "HAVE_READ")
    @Getter
    @Setter
    private boolean haveRead = false;

    @Column(name = "STATUS")
    @Getter
    @Setter
    private String status;

//    @NotNull
    @Column(name = "CLASS_ID")
    @Getter
    @Setter
    private Long classId;

//    @NotNull
    @Column(name = "CLASS_NAME")
    @Getter
    @Setter
    private String className;

    @Column(name = "professional_id")
    @Getter
    @Setter
    private Long professionalId;

    @Column(name = "college_id")
    @Getter
    @Setter
    private Long collegeId;

    @Column(name = "org_id")
    @Getter
    @Setter
    private Long orgId;

    @Column(name = "semester_id")
    @Getter
    @Setter
    private Long semesterId;
    @Column(name = "teaching_year")
    @Getter
    @Setter
    private String teachingYear;

    @Column(name = "STATUS2")
    @Getter
    @Setter
    private String status2;

    @Column(name = "gps_location2")
    @Getter
    @Setter
    private String gpsLocation2;

    @Column(name = "gps_detail2")
    @Getter
    @Setter
    private String gpsDetail2;

    @Column(name = "sign_time2")
    @Getter
    @Setter
    private Timestamp signTime2;

    @Column(name = "DEVICE_TOKEN2")
    @Getter
    @Setter
    private String deviceToken2;

    @Column(name = "GPS_TYPE2")
    @Getter
    @Setter
    private String gpsType2;

    @Column(name = "have_report2")
    @Getter
    @Setter
    private Integer haveReport2 = 0;

    @Column(name = "have_read2")
    @Getter
    @Setter
    private Boolean haveRead2 = false;

    public StudentSignIn() {}

    public StudentSignIn(CounsellorRollcall counsellorRollcall, Long studentId, String studentName, String studentNum, Long classId, String className, Long professionalId,
        Long collegeId, Long orgId, Long semesterId, String status, String teachingYear) {
        this.counsellorRollcall = counsellorRollcall;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentNum = studentNum;
        this.classId = classId;
        this.className = className;
        this.professionalId = professionalId;
        this.collegeId = collegeId;
        this.orgId = orgId;
        this.semesterId = semesterId;
        this.status = status;
        this.teachingYear = teachingYear;
    }

    public StudentSignIn(CounsellorRollcall counsellorRollcall, Long studentId, String studentName, String studentNum, Long classId, String className, Long professionalId,
                         Long collegeId, Long orgId, Long semesterId, String status, String status2, String teachingYear) {
        this.counsellorRollcall = counsellorRollcall;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentNum = studentNum;
        this.classId = classId;
        this.className = className;
        this.professionalId = professionalId;
        this.collegeId = collegeId;
        this.orgId = orgId;
        this.semesterId = semesterId;
        this.status = status;
        this.status2 = status2;
        this.teachingYear = teachingYear;
    }
}
