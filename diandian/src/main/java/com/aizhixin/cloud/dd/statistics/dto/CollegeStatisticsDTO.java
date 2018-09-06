package com.aizhixin.cloud.dd.statistics.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by LIMH on 2017/8/21.
 */
@ApiModel
@Data
public class CollegeStatisticsDTO {
    @ApiModelProperty(value = "学院名称")
    String collegeName;
    @ApiModelProperty(value = "点名完成率")
    String rollCallRate;
    @ApiModelProperty(value = "到课率")
    String schoolTimeRate;
    @ApiModelProperty(value = "好评率")
    String goodAssesRate;
    @ApiModelProperty(value = "班级统计")
    List <TeachingClassStatisticsDTO> classInfo;
}
