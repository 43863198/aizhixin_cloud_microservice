package com.aizhixin.cloud.orgmanager.company.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhen.pan on 2017/6/29.
 */
@ApiModel(description="学校管理员信息")
@ToString
public class AdminUserDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter  @Setter private Long id;
    @ApiModelProperty(value = "登录账号", position=2)
    @Getter  @Setter private String login;
    @ApiModelProperty(value = "密码(暂时无用)", position=3)
    @Getter  @Setter private String password;
    @ApiModelProperty(value = "姓名", position=4)
    @Getter  @Setter private String name;
    @ApiModelProperty(value = "学校ID", position=5)
    @Getter  @Setter private Long organId;
    @ApiModelProperty(value = "学校名称", position=6)
    @Getter  @Setter private String organName;

    public AdminUserDomain() {}

    public AdminUserDomain(Long id, String login, String name, Long organId) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.organId = organId;
    }
}
