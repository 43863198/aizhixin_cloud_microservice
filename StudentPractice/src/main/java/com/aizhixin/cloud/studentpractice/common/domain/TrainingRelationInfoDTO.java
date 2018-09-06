package com.aizhixin.cloud.studentpractice.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;



/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-19
 */
@ApiModel
@Data
public class TrainingRelationInfoDTO {
    @ApiModelProperty(value = "实训小组id")
    private Long id;
    @ApiModelProperty(value = "实训小组名称")
    private String name;
    @ApiModelProperty(value = "企业id")
    private Long enterpriseId;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "企业导师id")
    private Long corporateMentorsId;
    @ApiModelProperty(value = "企业导师账号id")
    private Long accountId;
    @ApiModelProperty(value = "企业导师姓名")
    private String corporateMentorsName;
    @ApiModelProperty(value = "企业导师手机号")
    private String corporateMentorsPhone;
    @ApiModelProperty(value = "学校id")
    private Long orgId;
    @ApiModelProperty(value = "学校老师id")
    private Long teacherId;
    @ApiModelProperty(value = "学校老师姓名")
    private String teacherName;
    @ApiModelProperty(value = "学校老师所属学院(院系)id")
    private Long tCollegeId;
    @ApiModelProperty(value = "学校老师所属学院(院系)名称")
    private String tCollegeName;
    @ApiModelProperty(value = "学生id")
    private List<CorporateMentorsInfoByStudentDTO> students;
}
