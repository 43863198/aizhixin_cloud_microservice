package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author LIMH
 */
@Entity(name = "DD_COUNSELLORROLLCALL")
@ToString
public class CounsellorRollcall extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPGROUP_ID")
    @Getter
    @Setter
    private TempGroup tempGroup;

    @NotNull
    @Column(name = "TEACHER_ID")
    @Getter
    @Setter
    private Long teacherId;

    @NotNull
    @Column(name = "TEACHER_NAME")
    @Getter
    @Setter
    private String teacherName;

    @NotNull
    @Column(name = "OPEN_TIME")
    @Getter
    @Setter
    private Timestamp openTime;

    @NotNull
    @Column(name = "STATUS")
    @Getter
    @Setter
    private Boolean status;

    public CounsellorRollcall() {}

    public CounsellorRollcall(TempGroup tempGroup, Long teacherId, String teacherName, Timestamp openTime, Boolean status) {
        this.tempGroup = tempGroup;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.openTime = openTime;
        this.status = status;
    }
}
