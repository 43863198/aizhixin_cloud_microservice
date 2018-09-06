package com.aizhixin.cloud.orgmanager.company.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="批量添加用户的输入信息")
@ToString
@NoArgsConstructor
public class BatchAddUserDomain {
    @ApiModelProperty( value="登录账号",dataType = "String",notes = "登录账号")
    @Getter @Setter  private String login;

    @ApiModelProperty( value="姓名",dataType = "String",notes = "姓名")
    @Getter @Setter  private String userName;

    @ApiModelProperty( value="角色组",dataType = "String",notes = "角色组")
    @Getter @Setter  private String roleGroup;

    @ApiModelProperty( value="缺省密码",dataType = "String",notes = "缺省密码")
    @Getter @Setter  private String password;

    public BatchAddUserDomain (String login, String userName, String roleGroup) {
        this.login = login;
        this.userName = userName;
        this.roleGroup = roleGroup;
    }
}
