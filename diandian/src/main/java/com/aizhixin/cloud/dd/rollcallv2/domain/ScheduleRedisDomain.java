package com.aizhixin.cloud.dd.rollcallv2.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author LIMH
 * @date 2018/1/11
 */

@ApiModel(description = "Redis或者缓存中课表信息")
@NoArgsConstructor
@ToString
public class ScheduleRedisDomain implements java.io.Serializable {
    @ApiModelProperty(value = "学校ID)")
    @Getter
    @Setter
    private Long orgId;
    @ApiModelProperty(value = "课程ID)")
    @Getter
    @Setter
    private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter
    @Setter
    private String courseName;
    @ApiModelProperty(value = "老师ID)")
    @Getter
    @Setter
    private Long teacherId;
    @ApiModelProperty(value = "老师名称")
    @Getter
    @Setter
    private String teacherName;
    @ApiModelProperty(value = "学期ID)")
    @Getter
    @Setter
    private Long semesterId;
    @ApiModelProperty(value = "学期名称")
    @Getter
    @Setter
    private String semesterName;
    @ApiModelProperty(value = "周ID")
    @Getter
    @Setter
    private Long weekId;
    @ApiModelProperty(value = "周名称")
    @Getter
    @Setter
    private String weekName;
    @ApiModelProperty(value = "第几周")
    @Getter
    @Setter
    private Integer weekNo;
    @ApiModelProperty(value = "星期几")
    @Getter
    @Setter
    private Integer dayOfWeek;
    @ApiModelProperty(value = "节ID")
    @Getter
    @Setter
    private Long periodId;
    @ApiModelProperty(value = "第几节")
    @Getter
    @Setter
    private Integer periodNo;
    @ApiModelProperty(value = "持续节")
    @Getter
    @Setter
    private Integer periodNum;
    @ApiModelProperty(value = "课堂开始时间")
    @Getter
    @Setter
    private String scheduleStartTime;
    @ApiModelProperty(value = "课堂结束时间")
    @Getter
    @Setter
    private String scheduleEndTime;
    @ApiModelProperty(value = "教室")
    @Getter
    @Setter
    private String classRoomName;
    @ApiModelProperty(value = "上课日期")
    @Getter
    @Setter
    private String teachDate;
    @ApiModelProperty(value = "教学班ID")
    @Getter
    @Setter
    private Long teachingclassId;
    @ApiModelProperty(value = "教学班名称")
    @Getter
    @Setter
    private String teachingclassName;
    @ApiModelProperty(value = "教学班编码")
    @Getter
    @Setter
    private String teachingclassCode;
    @ApiModelProperty(value = "开始时间（日期格式）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter
    @Setter
    private Date startDate;
    @ApiModelProperty(value = "结束时间（日期格式）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter
    @Setter
    private Date endDate;
    @ApiModelProperty(value = "到课率")
    @Getter
    @Setter
    private Double toClassRate;
    @ApiModelProperty(value = "课堂规则ID")
    @Getter
    @Setter
    private Long scheduleRollCallId;
    @ApiModelProperty(value = "课堂ID")
    @Getter
    @Setter
    private Long scheduleId;
    @ApiModelProperty(value = "临时调课ID")
    @Getter
    @Setter
    private Long tempScheduleId;
}