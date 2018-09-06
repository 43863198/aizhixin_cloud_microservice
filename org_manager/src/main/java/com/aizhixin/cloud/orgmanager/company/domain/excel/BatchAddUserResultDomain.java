package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="批量添加用户的返回信息")
@ToString
@NoArgsConstructor
public class BatchAddUserResultDomain {
    @ApiModelProperty( value="ID",dataType = "String",notes = "ID")
    @Getter @Setter  private Long id;

    @ApiModelProperty( value="登录账号",dataType = "String",notes = "登录账号")
    @Getter @Setter  private String login;
}
