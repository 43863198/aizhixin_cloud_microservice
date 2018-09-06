package com.aizhixin.cloud.dd.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by LIMH on 2017/11/24.
 */
@ApiModel(description = "课堂规则信息")
@NoArgsConstructor
@ToString
public class CurrentScheduleRulerDomain {
    @ApiModelProperty(value = "课堂ID")
    @Getter
    @Setter
    private Long scheduleId;
    @ApiModelProperty(value = "课堂规则ID")
    @Getter
    @Setter
    private Long rulerId;
    @ApiModelProperty(value = "课程ID")
    @Getter
    @Setter
    private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter
    @Setter
    private String courseName;
    @ApiModelProperty(value = "教学班名称")
    @Getter
    @Setter
    private String teachingclassName;
    @ApiModelProperty(value = "课堂状态(10课前，20课中，30课后)")
    @Getter
    @Setter
    private Integer classroomStatus = 10;
    @ApiModelProperty(value = "是否开启点名")
    @Getter
    @Setter
    private Boolean isOpenRollcall;
    @ApiModelProperty(value = "点名方式")
    @Getter
    @Setter
    private String rollCallType;
    @ApiModelProperty(value = "中值或者数字点名的数字值")
    @Getter
    @Setter
    private String localtion;
    @ApiModelProperty(value = "课堂迟到时间")
    @Getter
    @Setter
    private Integer courseLaterTime;
    @ApiModelProperty(value = "随堂点状态")
    @Getter
    @Setter
    private Integer classroomRollCall;
    @ApiModelProperty(value = "是否进入课堂(课前10，课中20，课后30)")
    @Getter
    @Setter
    private Integer isInClassroom;

}