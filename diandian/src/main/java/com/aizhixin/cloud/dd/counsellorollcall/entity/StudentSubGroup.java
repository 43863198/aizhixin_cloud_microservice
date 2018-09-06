package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by LIMH on 2017/11/29.
 * <p>
 * 分组学生表
 */
@Entity(name = "DD_STUDENTSUBGROUP")
@ToString
public class StudentSubGroup extends AbstractEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPGROUP_ID")
    @Getter
    @Setter
    private TempGroup tempGroup;

    @NotNull
    @Column(name = "STUDENT_ID")
    @Getter
    @Setter
    private Long studentId;

    public StudentSubGroup() {}

    public StudentSubGroup(TempGroup tempGroup, Long studentId) {
        this.tempGroup = tempGroup;
        this.studentId = studentId;
    }
}
