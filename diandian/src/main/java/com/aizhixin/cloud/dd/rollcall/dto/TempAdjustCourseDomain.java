package com.aizhixin.cloud.dd.rollcall.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "调停课基本信息")
@NoArgsConstructor
@ToString
public class TempAdjustCourseDomain {
    @ApiModelProperty(value = "第几周", position = 2)
    @Getter
    @Setter
    private Integer weekNo;

    @ApiModelProperty(value = "星期几（周日1，周六7）", position = 3)
    @Getter
    @Setter
    private Integer dayOfWeek;

    @ApiModelProperty(value = "停（加）课日期(yyyy-MM-dd)", position = 4)
    @Getter
    @Setter
    private String eventDate;

    @ApiModelProperty(value = "第几节", position = 5)
    @Getter
    @Setter
    private Integer periodNo;


    @ApiModelProperty(value = "持续节", position = 6)
    @Getter
    @Setter
    private Integer periodNum;

    @ApiModelProperty(value = "教室", position = 7)
    @Getter
    @Setter
    private String classroom;

}
