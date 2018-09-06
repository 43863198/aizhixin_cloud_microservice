package com.aizhixin.cloud.mobile.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@ApiModel(description = "移动端登录后获取的用户详细信息")
@NoArgsConstructor
@ToString
public class MobileUserInfo implements Serializable {
    @ApiModelProperty(value = "用户id")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "用户姓名")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "账号")
    @Getter @Setter private String login;
    @ApiModelProperty(value = "电话号码")
    @Getter @Setter private String phoneNumber;
    @ApiModelProperty(value = "邮箱")
    @Getter @Setter private String email;
    @ApiModelProperty(value = "学号/工号")
    @Getter @Setter private String personId;
    @ApiModelProperty(value = "学校ID")
    @Getter @Setter private Long organId;
    @ApiModelProperty(value = "学校名")
    @Getter @Setter private String organName;
    @ApiModelProperty(value = "学校编码")
    @Getter @Setter private String organCode;
    @ApiModelProperty(value = "企业名称")
    @Getter @Setter private String enterpriseName;
    @ApiModelProperty(value = "头像")
    @Getter @Setter private String avatar;
    @ApiModelProperty(value = "是否防作弊")
    @Getter @Setter private Boolean antiCheating = true;
    @ApiModelProperty(value = "班级")
    @Getter @Setter private Long classesId;
    @ApiModelProperty(value = "班级")
    @Getter @Setter private String classesName;
    @ApiModelProperty(value = "专业")
    @Getter @Setter private Long professionalId;
    @ApiModelProperty(value = "专业")
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "学院")
    @Getter @Setter private Long collegeId;
    @ApiModelProperty(value = "学院Code")
    @Getter @Setter private String collegeCode;
    @ApiModelProperty(value = "学院")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "服务端当前时间yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter @Setter private Date currentTime;
    @ApiModelProperty(value = "用户属于B or C or COM")
    @Getter @Setter private String groupType;
    @ApiModelProperty(value = "角色名(多个角色使用逗号分隔)")
    @Getter @Setter private String role;
    @ApiModelProperty(value = "年级")
    @Getter @Setter private String teachingYear;
    @ApiModelProperty(value = "身份证号")
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "生源地")
    @Getter @Setter private String studentSource;

    public void addRole(String roleName) {
        if (null == this.role) {
            this.role = roleName;
        } else {
            this.role = this.role + "," + roleName;
        }
    }
}
