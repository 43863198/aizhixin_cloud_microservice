package com.aizhixin.cloud.dd.counsellorollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @author LIMH
 */
@Data
@ApiModel(description = "导员点名组")
public class CounRollcallGroupDTO {
    @ApiModelProperty(value = "组ID")
    private Long tempGroupId;
    @ApiModelProperty(value = "组名称")
    private String tempGroupName;
    @ApiModelProperty(value = "定时点名时间")
    private String alarmTime;
    @ApiModelProperty(value = "定时点名模式")
    private String alarmModel;
    @ApiModelProperty(value = "定时点名开关")
    private Boolean alarmOnOff;

    @ApiModelProperty(value = "学校ID(选填)")
    Long orgId;
    @ApiModelProperty(value = "学院ID(选填)")
    Set<Long> collegeIds;
    @ApiModelProperty(value = "专业ID(选填)")
    Set<Long> proIds;
    @ApiModelProperty(value = "班级ID(选填)")
    Set<Long> classIds;
    @ApiModelProperty(value = "教学班ID(选填)")
    Set<Long> teachingClassIds;
    @ApiModelProperty(value = "组包含学生")
    Set<Long> studentList;

    public CounRollcallGroupDTO() {}

    public CounRollcallGroupDTO(String tempGroupName, Set<Long> studentList) {
        this.tempGroupName = tempGroupName;
        this.studentList = studentList;
    }
}
