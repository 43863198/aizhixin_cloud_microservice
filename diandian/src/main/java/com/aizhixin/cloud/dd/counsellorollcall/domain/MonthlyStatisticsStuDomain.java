package com.aizhixin.cloud.dd.counsellorollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ApiModel(description = "学生点名月统计列表")
@ToString
@Data
public class MonthlyStatisticsStuDomain {
    @ApiModelProperty(value = "学生id", dataType = "Long", notes = "学生id")
    private Long studentId;
    @ApiModelProperty(value = "学生姓名", dataType = "String", notes = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "班级名称", dataType = "String", notes = "班级名称")
    private String className;
    @ApiModelProperty(value = "头像", dataType = "String", notes = "头像")
    private String avatar;
    @ApiModelProperty(value = "签到次数", dataType = "Integer", notes = "头像")
    private Integer count;
}
