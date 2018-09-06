package com.aizhixin.cloud.orgmanager.training.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel
@Data
public class TrainingCountDTO {
    @ApiModelProperty(value = "指导教师数量")
    private Long classTeacherTotal;
    @ApiModelProperty(value = "企业导师数量")
    private Long mentorTotal;
    @ApiModelProperty(value = "学生数量")
    private Long stuTotal;
    @ApiModelProperty(value = "参与计划未结束数量")
    private Long groupNotOverTotal;
    @ApiModelProperty(value = "参与计划已结束数量")
    private Long groupEndTotal;

}
