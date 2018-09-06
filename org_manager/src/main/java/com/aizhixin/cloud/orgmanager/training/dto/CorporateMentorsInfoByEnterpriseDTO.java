package com.aizhixin.cloud.orgmanager.training.dto;

import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;

import java.util.Date;
import java.util.List;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
@NoArgsConstructor
public class CorporateMentorsInfoByEnterpriseDTO {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "账号id")
    private Long accountId;
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "工号")
    private String jobNumber;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "企业地址")
    private String companyAddress;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "职位")
    private String position;
    @ApiModelProperty(value = "邮箱")
    private String mailbox;
    @ApiModelProperty(value = "手机")
    private  String phone;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "实训小组id")
    private Long trainingGroupId;
    @ApiModelProperty(value = "实训小组名称")
    private String trainingGroupName;
    @ApiModelProperty(value = "实训小组开始时间")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date starDate;
    @ApiModelProperty(value = "实训小组结束时间")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endDate;
    @ApiModelProperty(value = "学校老师id")
    private Long teacherId;
    @ApiModelProperty(value = "学校老师姓名")
    private  String teacherName;
    @ApiModelProperty(value = "学校老师工号")
    private String teacherJobNumer;
    @ApiModelProperty(value = "学校老师手机号")
    private String teacherPhone;
    @ApiModelProperty(value = "学校老师性别")
    private String teacherSex;
    @ApiModelProperty(value = "学校老师所在学院")
    private  String collegeName;
    @ApiModelProperty(value = "学生信息")
    private List<StudentDTO> studentDTOList;
    @ApiModelProperty(value = "实践参与计划设置")
    private TrainingGropSetDTO setDTO = new TrainingGropSetDTO();

    public CorporateMentorsInfoByEnterpriseDTO(Long trainingGroupId, String trainingGroupName, Long teacherId,String teacherName){
        this.trainingGroupId = trainingGroupId;
        this.trainingGroupName = trainingGroupName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }



}
