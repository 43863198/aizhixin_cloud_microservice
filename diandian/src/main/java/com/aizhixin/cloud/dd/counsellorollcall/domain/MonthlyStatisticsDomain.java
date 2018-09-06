package com.aizhixin.cloud.dd.counsellorollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ApiModel(description = "月统计")
@ToString
@Data
public class MonthlyStatisticsDomain {
    @ApiModelProperty(value = "班级名称", dataType = "String", notes = "班级名称")
    private String className;
    @ApiModelProperty(value = "学生签到列表", dataType = "String", notes = "学生签到列表")
    private List<MonthlyStatisticsStuDomain> students;
}
