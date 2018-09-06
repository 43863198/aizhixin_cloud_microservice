package com.aizhixin.cloud.orgmanager.company.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zhen.pan on 2017/6/29.
 */
@ApiModel(description="学校管理员信息")
@ToString
public class UserAdminDomain {
    @NotNull
    @ApiModelProperty(value = "登录账号", required = true, position=1)
    @Size(min = 6, max = 50)
    @Getter @Setter  private String login;

    @NotNull
    @ApiModelProperty(value = "登录密码", required = true, position=2)
    @Size(min = 6, max = 50)
    @Getter @Setter  private String password;

    @NotNull
    @ApiModelProperty(value = "学校ID", required = true, position=3, allowableValues = "range[1,infinity]")
    @Getter @Setter  private Long   organId;

    @ApiModelProperty(value = "学院ID", required = true, position=4, allowableValues = "range[1,infinity]")
    @Getter @Setter  private Long   collegeId;
}
