package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "问卷分配到人详情")
@Data
public class QuestionnaireAssignUserDTO {
    @ApiModelProperty(value = "用户id 删除分配时必填")
    private Long userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "学号工号")
    private String jobNum;
    @ApiModelProperty(value = "学院id")
    private Long collegeId;
    @ApiModelProperty(value = "学院名称")
    private String collegeName;
    @ApiModelProperty(value = "70：学生 60：教师")
    private Integer userType;
    @ApiModelProperty(value = "10：辅导员 20：授课教师")
    private Integer teacherType;
    @ApiModelProperty(value = "权重")
    private Long weight;
}
