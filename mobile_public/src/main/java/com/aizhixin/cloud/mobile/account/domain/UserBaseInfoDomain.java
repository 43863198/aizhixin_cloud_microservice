package com.aizhixin.cloud.mobile.account.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ApiModel
@NoArgsConstructor
@ToString
public class UserBaseInfoDomain implements java.io.Serializable {
    @ApiModelProperty( value="用户ID",notes = "用户ID")
    @Getter @Setter private Long id;
    @ApiModelProperty( value="登录账号",dataType = "String",notes = "登录账号")
    @Getter @Setter private String login;
    @ApiModelProperty( value="姓名",dataType = "String",notes = "姓名")
    @Getter @Setter private String userName;
    @ApiModelProperty( value="头像地址",dataType = "String",notes = "头像地址")
    @Getter @Setter private String avatar;
    @ApiModelProperty( value="电话号码",dataType = "String",notes = "电话号码")
    @Getter @Setter private String phoneNumber;
    @ApiModelProperty( value="邮箱地址",dataType = "String",notes = "格式为xxx@xxx")
    @Getter @Setter private String email;
    @ApiModelProperty( value="角色组",dataType = "String",notes = "角色组")
    @Getter @Setter private String roleGroup;
    @ApiModelProperty( value="角色",notes = "角色")
    @Getter @Setter private List<String> roles;

//    @ApiModelProperty( value="密码",dataType = "String",notes = "密码")
//    @Getter @Setter private String password;
//    @ApiModelProperty( value="组类型",dataType = "String",notes = "组类型")
//    @Getter @Setter private String groupType;
//    @ApiModelProperty( value="是否激活",notes = "是否激活")
//    @Getter @Setter private Boolean activated;
//    @ApiModelProperty( value="状态",dataType = "String",notes = "状态")
//    @Getter @Setter private String status;
    @ApiModelProperty( value="是否电话激活",notes = "是否电话激活")
    @Getter @Setter private Boolean phoneActivated;
    @ApiModelProperty( value="是否邮箱激活",notes = "是否邮箱激活")
    @Getter @Setter private Boolean mailActivated;
//    @ApiModelProperty( value="电话激活时间",notes = "电话激活时间")
//    @Getter @Setter private Long validPhoneTime;
//    @ApiModelProperty( value="邮箱激活时间",notes = "邮箱激活时间")
//    @Getter @Setter private Long validEmailTime;
//    @ApiModelProperty( value="创建人",notes = "创建人")
//    @Getter @Setter private String createdBy;
//    @ApiModelProperty( value="最后修改人",notes = "最后修改人")
//    @Getter @Setter private String lastModifiedBy;
//    @ApiModelProperty( value="创建时间",notes = "创建时间")
//    @Getter @Setter private Long createdDate;
//    @ApiModelProperty( value="最后修改时间",notes = "最后修改时间")
//    @Getter @Setter private Long lastModifiedDate;
}
