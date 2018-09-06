package com.aizhixin.cloud.dd.communication.entity;


import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@ApiModel(description = "拨打电话记录")
@Entity(name = "DD_CALLRECORDS")
@ToString
public class CallRecords extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "STUDENT_ID")
    @Getter
    @Setter
    Long   studentId;

    @NotNull
    @Column(name = "STUDENT_PHONE")
    @Getter
    @Setter
    String studentPhone;

    @NotNull
    @Column(name = "CALLEDSTUDENT_ID")
    @Getter
    @Setter
    Long   calledStudentId;

    @NotNull
    @Column(name = "CALLED_PHONE")
    @Getter
    @Setter
    String calledStudentPhone;


    @NotNull
    @Column(name = "CALLED_TIME")
    @Getter
    @Setter
    String calledTime;

}
