package com.aizhixin.cloud.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@ApiModel(description="Redis或者缓存中课表信息")
@NoArgsConstructor
@ToString
public class StudentInfoDTO {
    @ApiModelProperty(value = "学生ID)")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "学生名称)")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "学生学号)")
    @Getter @Setter private String jobNumber;
    @ApiModelProperty(value = "教学班ID")
    @Getter @Setter private Long teachingClassId;
}
