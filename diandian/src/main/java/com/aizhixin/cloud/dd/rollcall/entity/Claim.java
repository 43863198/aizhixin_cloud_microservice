package com.aizhixin.cloud.dd.rollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by LIMH on 2017/11/7.
 */
@Entity
@Table(name = "DD_CLAIM")
public class Claim extends AbstractEntity {
    @NotNull
    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId; //

    @NotNull
    @Column(name = "TEACHER_NAME")
    @Getter
    @Setter
    private String teacherName; //

    @NotNull
    @Column(name = "TEACHINGCLASS_ID")
    @Getter
    @Setter
    private Long teachingclassId; //

    @NotNull
    @Column(name = "TEACH_DATE")
    @Getter
    @Setter
    private String teachDate; //

    @NotNull
    @Column(name = "PERIOD_NO")
    @Getter
    @Setter
    private Integer periodNo; //

    @NotNull
    @Column(name = "PERIOD_NUM")
    @Getter
    @Setter
    private Integer periodNum; //


}
