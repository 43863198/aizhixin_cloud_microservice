package com.aizhixin.cloud.orgmanager.company.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhen.pan on 2017/12/19.
 */
@ApiModel(description="新生信息")
@ToString
@NoArgsConstructor
public class NewStudentDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "姓名", position=2)
    @Getter  @Setter private String name;
    @ApiModelProperty(value = "手机号码", position=3)
    @Getter  @Setter private String phone;
    @ApiModelProperty(value = "专业ID", position = 4)
    @Getter  @Setter private Long professionalId;
    @ApiModelProperty(value = "专业名称", position = 5)
    @Getter  @Setter private String professionalName;
    @ApiModelProperty(value = "学院ID", position = 6)
    @Getter  @Setter private Long collegeId;
    @ApiModelProperty(value = "学院名称", position = 7)
    @Getter  @Setter private String collegeName;
    @ApiModelProperty(value = "学校ID", position=8)
    @Getter  @Setter private Long orgId;
    @ApiModelProperty(value = "学校名称", position=9)
    @Getter  @Setter private String orgName;

    @ApiModelProperty(value = "身份证号", position=10)
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "生源地", position=11)
    @Getter @Setter private String studentSource;
    @ApiModelProperty(value = "学生类别(单招、统招)", position=12)
    @Getter @Setter private String studentType;
    @ApiModelProperty(value = "录取通知书号", position=13)
    @Getter @Setter private String admissionNoticeNumber;
    @ApiModelProperty(value = "层次(专科、本科)", position=16)
    @Getter @Setter private String eduLevel;

    @ApiModelProperty(value = "学号", position=14)
    @Getter @Setter private String jobNumber;
    @ApiModelProperty(value = "账号是否被激活", position=15)
    @Getter @Setter private Boolean activated = Boolean.FALSE;
}
