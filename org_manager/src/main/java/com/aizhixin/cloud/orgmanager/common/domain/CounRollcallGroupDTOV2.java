package com.aizhixin.cloud.orgmanager.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(description = "导员点名组")
public class CounRollcallGroupDTOV2 {
    @ApiModelProperty(value = "组ID")
    private Long tempGroupId;
    @ApiModelProperty(value = "组名称")
    private String tempGroupName;
    @ApiModelProperty(value = "定时点名时间")
    private String alarmTime ="9:00";
    @ApiModelProperty(value = "定时点名模式")
    private String alarmModel ="1,2,3,4,5";
    @ApiModelProperty(value = "定时点名开关")
    private Boolean alarmOnOff;
    @ApiModelProperty(value = "规则id")
    private Long ruleId;
    @ApiModelProperty(value = "点名次数 1：1次 2：2次")
    private Integer rollcallNum =1;
    @ApiModelProperty(value = "点名类型 默认其它 10:早操 20:晚自习 30:晚查寝 40:外出实习 50:其它")
    private Integer rollcallType =40;

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
    @ApiModelProperty(value = "实践id")
    private Long practiceId;

    public CounRollcallGroupDTOV2() {
    }

    public CounRollcallGroupDTOV2(String tempGroupName, Set<Long> studentList) {
        this.tempGroupName = tempGroupName;
        this.studentList = studentList;
    }
}
