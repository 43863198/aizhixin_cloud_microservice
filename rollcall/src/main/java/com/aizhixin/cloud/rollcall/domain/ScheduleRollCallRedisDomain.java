package com.aizhixin.cloud.rollcall.domain;


import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="Redis或者缓存中课堂规则信息(课堂点名规则，课堂设置)")
@NoArgsConstructor
@ToString
public class ScheduleRollCallRedisDomain implements java.io.Serializable {
    @ApiModelProperty(value = "课堂规则ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "课堂ID")
    @Getter @Setter private Long scheduleId;
    @ApiModelProperty(value = "课程ID")
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String teachingclassName;
    @ApiModelProperty(value = "是否开启点名(用来标识打卡机)")
    @Getter @Setter private Boolean isOpenRollcall;
    @ApiModelProperty(value = "点名方式")
    @Getter @Setter private String rollCallType;
    @ApiModelProperty(value = "中值或者数字点名的数字值")
    @Getter @Setter private String localtion;
    @ApiModelProperty(value = "课堂迟到时间")
    @Getter @Setter private Integer courseLaterTime;
    @ApiModelProperty(value = "随堂点状态")
    @Getter @Setter private Integer classroomRollCall = 10;
    @ApiModelProperty(value = "是否进入课堂(课前10，课中20，课后30)")
    @Getter @Setter private Integer isInClassroom = CourseRollCallConstants.COURSE_BEFORE;
    @ApiModelProperty(value = "课堂到课率")
    @Getter @Setter private String attendance;
}
