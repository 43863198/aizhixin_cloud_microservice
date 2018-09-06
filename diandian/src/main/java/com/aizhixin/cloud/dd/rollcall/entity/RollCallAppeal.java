package com.aizhixin.cloud.dd.rollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "dd_rollcall_appeal")
@ToString
public class RollCallAppeal extends AbstractOnlyIdAndCreatedDateEntity {

    @ManyToOne(cascade = {CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "schedule_rollcall_id")
    @Getter
    @Setter
    private ScheduleRollCall scheduleRollCall;

    @ManyToOne(cascade = {CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "rollcall_id")
    @Getter
    @Setter
    private RollCall rollCall;

    @Column(name = "student_id")
    @Getter
    @Setter
    private Long stuId;

    @Column(name = "content")
    @Getter
    @Setter
    private String content;

    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId;

    @Column(name = "teacher_name")
    @Getter
    @Setter
    private String teacherName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "appealId")
    @Getter
    @Setter
    private List<RollCallAppealFile> appealFiles;

    //10:等待审核 20:申诉成功 30:申诉失败
    @Column(name = "appeal_status")
    @Getter
    @Setter
    private Integer appealStatus;

    @Column(name = "appeal_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    protected Date appealDate;
}
