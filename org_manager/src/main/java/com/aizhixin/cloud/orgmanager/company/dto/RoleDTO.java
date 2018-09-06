package com.aizhixin.cloud.orgmanager.company.dto;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-04-02
 */
@Data
@ApiModel(description="角色")
public class RoleDTO {
    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色组")
    private String roleGroup;
    @ApiModelProperty(value = "角色描述")
    private String roleDescribe;
}
