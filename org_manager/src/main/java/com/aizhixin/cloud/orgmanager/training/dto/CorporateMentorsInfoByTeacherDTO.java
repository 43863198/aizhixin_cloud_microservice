package com.aizhixin.cloud.orgmanager.training.dto;

import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
@NoArgsConstructor
public class CorporateMentorsInfoByTeacherDTO {
    @ApiModelProperty(value = "学校老师id")
    private Long id;
    @ApiModelProperty(value = "学校老师姓名")
    private String Name;
    @ApiModelProperty(value = "实训小组id")
    private Long trainingGroupId;
    @ApiModelProperty(value = "实训小组名称")
    private String trainingGroupName;
    @ApiModelProperty(value = "企业导师id")
    private Long corporateMentorsId;
    @ApiModelProperty(value = "企业导师姓名")
    private  String CorporateMentorsName;
    private  String teacherName;
    @ApiModelProperty(value = "学生信息")
    private List<StudentDTO> studentDTOList;

}
