/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhen.pan
 */
@ApiModel(description = "用户备份信息")
@NoArgsConstructor
@ToString
public class StudentBackDomain {
    @ApiModelProperty(value = "ID", position = 1)
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "userId", position = 2)
    @Getter @Setter private Long userId;
    @ApiModelProperty(value = "姓名",position = 4)
    @Getter @Setter private String name;
    @ApiModelProperty(value = "姓名电话", position = 5)
    @Getter @Setter private String phone;
    @ApiModelProperty(value = "到备份表的原因", position = 6)
    @Getter @Setter private String cause;
    @ApiModelProperty(value = "学号", position = 7)
    @Getter @Setter  private String jobNumber;
    @ApiModelProperty(value = "性别(男|女)", position = 8)
    @Getter @Setter private String sex;
    @ApiModelProperty(value = "班级ID", position = 9)
    @Getter @Setter private Long classesId;
    @ApiModelProperty(value = "班级名称", position = 10)
    @Getter @Setter private String classesName;
    @ApiModelProperty(value = "专业ID",position = 11)
    @Getter @Setter private Long professionalId;
    @ApiModelProperty(value = "专业名称", position = 12)
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "学院ID", position = 13)
    @Getter @Setter private Long collegeId;
    @ApiModelProperty(value = "学院名称", position = 14)
    @Getter @Setter private String collegeName;

    public StudentBackDomain(Long id, String cause, Long userId, String name, String phone, String jobNumber, String sex, Long classesId, String classesName, Long professionalId, String professionalName, Long collegeId, String collegeName) {
        this.id = id;
        this.cause = cause;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.jobNumber = jobNumber;
        this.sex = sex;
        this.classesId = classesId;
        this.classesName = classesName;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
    }
}
