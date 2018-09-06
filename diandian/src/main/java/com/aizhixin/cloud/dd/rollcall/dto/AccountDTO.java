package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import lombok.Data;

import org.hibernate.validator.constraints.Email;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;

@ApiModel(description = "班级信息")
@Data
public class AccountDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", required = false)
    private Long id;

    private String sex;
    private String name;

    @ApiModelProperty(value = "账号", required = false)
    private String login;

    @Size(max = 50)
    @ApiModelProperty(value = "电话号码", required = false)
    private String phoneNumber;

    @Email
    @ApiModelProperty(value = "邮箱", required = false)
    @Size(min = 5, max = 100)
    private String email;

    @ApiModelProperty(value = "学号/工号", required = false)
    private String personId;

    @ApiModelProperty(value = "学校ID", required = false)
    private Long organId;

    @ApiModelProperty(value = "权限名", required = false)
    private String role;

    @ApiModelProperty(value = "学校名", required = false)
    private String organName;

    @ApiModelProperty(value = "企业名称", required = false)
    private String enterpriseName;
    //	@ApiModelProperty(value = "学校logo", required = false)
//	private String organLogo;
//
    @ApiModelProperty(value = "头像", required = false)
    private String avatar;

    @ApiModelProperty(value = "知新名", required = false)
    private String shortName;


    @ApiModelProperty(value = "用户属于B or C", required = false)
    private String groupType;
    @ApiModelProperty(value = "身份证", required = false)
    private String idNumber;
//
//	@ApiModelProperty(value = "正方形logo")
//	private String ptLogo;
//
//	@ApiModelProperty(value = "长方形logo")
//	private String lptLogo;

    private Boolean antiCheating = true;

    private Long classesId;
    private String classesName;
    private Long professionalId;
    private String professionalName;
    private Long collegeId;
    private String collegeName;

    private String teachingYear;
    private String currentTime = DateFormatUtil.format(new Date(),
            DateFormatUtil.FORMAT_LONG);

}
