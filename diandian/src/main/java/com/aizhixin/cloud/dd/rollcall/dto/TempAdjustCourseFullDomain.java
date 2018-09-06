package com.aizhixin.cloud.dd.rollcall.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="调停课基本信息")
@NoArgsConstructor
@ToString
public class TempAdjustCourseFullDomain {
//    @ApiModelProperty(value = "学校", position=1)
//    @Getter @Setter private Long orgId;
//
//    @ApiModelProperty(value = "学期", position=2)
//    @Getter @Setter private Long semesterId;

    @ApiModelProperty(value = "教学班", position=3)
    @Getter @Setter private Long teachingClassId;

    @ApiModelProperty(value = "删除课", position=4)
    @Getter @Setter private TempAdjustCourseDomain deleteTempCourseDomain;

    @ApiModelProperty(value = "增加课", position=5)
    @Getter @Setter private TempAdjustCourseDomain addTempAdjustCourseDomain;

    @ApiModelProperty(value = "操作人", position=6)
    @Getter @Setter private Long userId;
}
