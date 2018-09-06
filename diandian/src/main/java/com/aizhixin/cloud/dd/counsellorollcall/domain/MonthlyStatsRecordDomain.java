package com.aizhixin.cloud.dd.counsellorollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "学生点名月统计记录")
@ToString
@Data
public class MonthlyStatsRecordDomain {
    @ApiModelProperty(value = "stuId")
    private Long stuId;
    @ApiModelProperty(value = "groupId")
    private Long groupId;
    @ApiModelProperty(value = "date")
    private String date;
    @ApiModelProperty(value = "label")
    private String label;
}
