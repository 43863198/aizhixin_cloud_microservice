package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by LIMH on 2017/11/29.
 * <p>
 * 分组表
 */
@Entity(name = "DD_TEMPGROUP")
@ToString
public class TempGroup extends AbstractEntity {

    @NotNull
    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

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
    @Column(name = "SUBGROUP_NUM")
    @Getter
    @Setter
    private Integer subGroupNum;

    @NotNull
    @Column(name = "STATUS")
    @Getter
    @Setter
    private Boolean status;

    @NotNull
    @Column(name = "ORGAN_ID")
    @Getter
    @Setter
    private Long orgId;

    @Column(name = "rule_id")
    @Getter
    @Setter
    private Long ruleId;

    @Column(name = "rollcall_num")
    @Getter
    @Setter
    private Integer rollcallNum;

    @Column(name = "rollcall_type")
    @Getter
    @Setter
    private Integer rollcallType;

    @Column(name = "message_id")
    @Getter
    @Setter
    private String messageId;

    @Column(name = "practice_id")
    @Getter
    @Setter
    private Long practiceId;

    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId;

    @Column(name = "stu_id")
    @Getter
    @Setter
    private Long stuId;


    public TempGroup() {

    }

    public TempGroup(String name, Long teacherId, String teacherName, Integer subGroupNum, Boolean status, Long orgId, String messageId) {
        this.name = name;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.subGroupNum = subGroupNum;
        this.status = status;
        this.orgId = orgId;
        this.messageId = messageId;
    }

    public TempGroup(String name, Long teacherId, String teacherName, Integer subGroupNum, Boolean status, String messageId) {
        this.name = name;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.subGroupNum = subGroupNum;
        this.status = status;
        this.messageId = messageId;
    }
}
