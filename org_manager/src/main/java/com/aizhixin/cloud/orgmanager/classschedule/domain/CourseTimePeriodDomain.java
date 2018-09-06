package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhen.pan on 2017/5/2.
 */
@ApiModel(description="课堂排课信息")
@ToString
public class CourseTimePeriodDomain {
    @ApiModelProperty(value = "星期几（周日1，周六7）", required = true)
    @Getter @Setter private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始ID", required = true)
    @Getter @Setter private Long periodId;
    @ApiModelProperty(value = "从第几节开始，序号")
    @Getter @Setter private Integer periodMo;
    @ApiModelProperty(value = "总共持续几个小节", required = true)
    @Getter @Setter private Integer periodNum;
    @ApiModelProperty(value = "起始周，第几周ID", required = true)
    @Getter @Setter private Long startWeekId;
    @ApiModelProperty(value = "结束周，第几周ID", required = true)
    @Getter @Setter private Long endWeekId;
    @ApiModelProperty(value = "起始周，第几周序号")
    @Getter @Setter private Integer startWeekNo;
    @ApiModelProperty(value = "结束周，第几周序号")
    @Getter @Setter private Integer endWeekNo;
    @ApiModelProperty(value = "单周或双周,10不区分单双周,20单周,30双周", required = true)
    @Getter @Setter private Integer singleOrDouble;
    @ApiModelProperty(value = "教室")
    @Getter @Setter private String classroom;
    @ApiModelProperty(value = "备注")
    @Getter @Setter private String remark;
    @ApiModelProperty(value = "颜色")
    @Getter @Setter private String color;
}
