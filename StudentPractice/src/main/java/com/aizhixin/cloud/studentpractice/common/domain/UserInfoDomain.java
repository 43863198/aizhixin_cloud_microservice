package com.aizhixin.cloud.studentpractice.common.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@ApiModel(description="用户信息,兼容知新用户信息接口")
@ToString
@NoArgsConstructor
public class UserInfoDomain {
    @ApiModelProperty(value = "用户ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "账号ID")
    @Getter @Setter private Long accountId;
    @ApiModelProperty(value = "用户姓名")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "登录账号")
    @Getter @Setter private String login;
    @ApiModelProperty(value = "电话")
    @Getter @Setter private String phone;
    @ApiModelProperty(value = "邮箱")
    @Getter @Setter private String mail;
    @ApiModelProperty(value = "用户头像")
    @Getter @Setter private String avatar;
    @ApiModelProperty(value = "用户组")
    @Getter @Setter private String userGroup;
    @ApiModelProperty(value = "性别")
    @Getter @Setter private String gender;
    @ApiModelProperty(value = "学号/工号")
    @Getter @Setter private String workNo;
    @ApiModelProperty(value = "角色数组")
    @Getter @Setter private Set<String> roleNames;
    @ApiModelProperty(value = "班级ID")
    @Getter @Setter private Long classId;
    @ApiModelProperty(value = "班级名称")
    @Getter @Setter private String className;
    @ApiModelProperty(value = "年级")
    @Getter @Setter private String teachingYear;
    @ApiModelProperty(value = "专业ID")
    @Getter @Setter private Long majorId;
    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String majorName;
    @ApiModelProperty(value = "学院ID")
    @Getter @Setter private Long collegeId;
    @ApiModelProperty(value = "学院名称")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "学校ID")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "学校名称")
    @Getter @Setter private String orgName;
    @ApiModelProperty(value = "学校编码")
    @Getter @Setter private String orgCode;
    @ApiModelProperty(value = "学校域名")
    @Getter @Setter private String orgDomainName;
    @ApiModelProperty(value = "学校Logo")
    @Getter @Setter private String orgLogo;
    @ApiModelProperty(value = "身份证号", position=25)
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "生源地", position=26)
    @Getter @Setter private String studentSource;
    @ApiModelProperty( value="是否电话激活",notes = "是否电话激活")
    @Getter @Setter private Boolean phoneActivated;
    @ApiModelProperty( value="是否邮箱激活",notes = "是否邮箱激活")
    @Getter @Setter private Boolean mailActivated;

    public void addRole(String role) {
        if (null == this.roleNames) {
            this.roleNames = new HashSet<>();
        }
        this.roleNames.add(role);
    }
}