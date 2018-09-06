package com.aizhixin.cloud.orgmanager.comminication.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="用户账号信息")
@NoArgsConstructor
@ToString
public class UserinfoDTO {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "LOGIN", position=2)
    @Getter @Setter private String login;
    @ApiModelProperty(value = "姓名", position=3)
    @Getter @Setter private String name;
    @ApiModelProperty(value = "邮箱", position=4)
    @Getter @Setter private String email;
    @ApiModelProperty(value = "手机", position=5)
    @Getter @Setter private String phoneNumber;
    @ApiModelProperty(value = "头像", position=6)
    @Getter @Setter private String avatar;
    @ApiModelProperty(value = "PWD", position=7)
    @Getter @Setter private String password;
    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter private String groupType;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Long createdDate;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Long lastModifiedDate;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Boolean activated;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String status;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Boolean validPhone;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Boolean validEmail;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Long validPhoneTime;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter Long validEmailTime;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String shortName;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String android;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String ios;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String androidTeacher;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String iosTeacher;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String langKey;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String authorities;

    @ApiModelProperty(value = "用户组", position=8)
    @Getter @Setter String userName;
}
