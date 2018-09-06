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
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
public class CorporateMentorsInfoByStudentDTO {
    @ApiModelProperty(value = "学生id")
    private Long sid;
    @ApiModelProperty(value = "学校id")
    private Long orgId;
    @ApiModelProperty(value = "学生姓名")
    private String sname;
    @ApiModelProperty(value = "学生性别")
    private String sex;
    @ApiModelProperty(value = "学生学号")
    private String sjobNumber;
    @ApiModelProperty(value = "学生所属院系")
    private String collegeName;
    @ApiModelProperty(value = "学生所属院系id")
    private Long collegeId;
    @ApiModelProperty(value = "学生所属专业")
    private String professionalName;
    @ApiModelProperty(value = "学生所属专业id")
    private Long professionalId;
    @ApiModelProperty(value = "学生班级")
    private String classesName;
    @ApiModelProperty(value = "学生班级id")
    private Long classesId;
    @ApiModelProperty(value = "学生手机号")
    private String sphone;
    @ApiModelProperty(value = "实训小组id")
    private Long trainingGroupId;
    @ApiModelProperty(value = "实训小组名称")
    private String trainingGroupName;
    @ApiModelProperty(value = "学校老师id")
    private Long teacherId;
    @ApiModelProperty(value = "学校老师姓名")
    private  String teacherName;
    @ApiModelProperty(value = "学校老师工号")
    private String tjobNumber;
    @ApiModelProperty(value = "企业导师id")
    private Long cid;
    @ApiModelProperty(value = "企业导师账号")
    private String jobNumber;
    @ApiModelProperty(value = "企业导师姓名")
    private String cname;
    @ApiModelProperty(value = "账号id")
    private Long accountId;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "企业地址")
    private String companyAddress;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "职位")
    private String position;
    @ApiModelProperty(value = "企业导师邮箱")
    private String mailbox;
    @ApiModelProperty(value = "企业导师手机")
    private  String phone;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
}
