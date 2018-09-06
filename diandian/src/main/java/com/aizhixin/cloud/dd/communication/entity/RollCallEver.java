package com.aizhixin.cloud.dd.communication.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@ApiModel(description = "随时点班級签到表")
@Entity(name = "dd_rollcallever")
@ToString
public class RollCallEver extends AbstractEntity {

    private static final long serialVersionUID = 1L;


    @NotNull
    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId;


    @NotNull
    @Column(name = "open_time")
    @Getter
    @Setter
    private Timestamp openTime;

    @NotNull
    @Column(name = "class_ids")
    @Getter
    @Setter
    private String classIds;


    @NotNull
    @Column(name = "status")
    @Getter
    @Setter
    private boolean status;



}
