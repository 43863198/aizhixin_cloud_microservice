package com.aizhixin.cloud.orgmanager.comminication.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="学生联系人信息")
@NoArgsConstructor
@ToString
public class StudentContactDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private Long id = 0L;
    @ApiModelProperty(value = "姓名", position=2)
    @Getter @Setter private String name = "";
    @ApiModelProperty(value = "电话", position=3)
    @Getter @Setter private String phone = "";
    @ApiModelProperty(value = "工号、学号", position=4)
    @Getter @Setter private String stuId = "";
    @ApiModelProperty(value = "性别", position=5)
    @Getter @Setter private String sex = "";
    @ApiModelProperty(value = "角色", position=6)
    @Getter @Setter private String role = "";
    @ApiModelProperty(value = "用户头像", position=7)
    @Getter @Setter private String avatar = "";
}
