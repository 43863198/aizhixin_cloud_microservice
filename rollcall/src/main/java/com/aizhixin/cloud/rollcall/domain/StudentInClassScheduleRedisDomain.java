package com.aizhixin.cloud.rollcall.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "学生课堂内的课堂信息")
@NoArgsConstructor
@ToString
public class StudentInClassScheduleRedisDomain implements java.io.Serializable {
    @ApiModelProperty(value = "课堂规则ID")
    @Getter @Setter private Long scheduleRollCallId;
    @ApiModelProperty(value = "课堂ID")
    @Getter @Setter private Long scheduleId;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private Long studentId;
}
