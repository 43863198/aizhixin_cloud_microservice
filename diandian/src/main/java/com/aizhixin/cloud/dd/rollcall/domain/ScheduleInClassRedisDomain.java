package com.aizhixin.cloud.dd.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by LIMH on 2017/11/10.
 */
@ApiModel(description = "课堂内的课堂信息")
@NoArgsConstructor
@Data
public class ScheduleInClassRedisDomain implements java.io.Serializable {
    @ApiModelProperty(value = "课堂规则ID")
    private Long scheduleRollCallId;
    @ApiModelProperty(value = "课堂ID")
    private Long scheduleId;
}
