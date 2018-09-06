package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-11
 */
@Data
@ApiModel(description="角色分配列表信息")
@AllArgsConstructor
public class TeacherUserRoleDomain {
    @ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position=1)
    private Long id;
    @NotNull
    @ApiModelProperty(value = "姓名", required = true, position=4)
    @Size(min = 0, max = 50)
    private String name;
    @ApiModelProperty(value = "姓名电话", position=5)
    @Size(min = 0, max = 20)
    private String phone;
    @ApiModelProperty(value = "工号", position=7)
    @Size(min = 0, max = 50)
    private String jobNumber;
    @ApiModelProperty(value = "性别(男性male|女性female)", allowableValues = "male,female", position=8)
    @Size(min = 0, max = 10)
    private String sex;
    @ApiModelProperty(value = "学院名称", position=14)
    private String collegeName;
    @ApiModelProperty(value = "角色权限", position=14)
    private String roleName;
    @ApiModelProperty(value = "分配日期", position=16)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lastModifiedDate;
    @ApiModelProperty(value = "分配人", position=14)
    private Long lastModifiedBy;
}
