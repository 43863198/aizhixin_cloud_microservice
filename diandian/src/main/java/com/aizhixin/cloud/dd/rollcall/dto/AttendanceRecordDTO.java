package com.aizhixin.cloud.dd.rollcall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-16
 */
@Data
@ApiModel(description = "考勤记录比例")
@EqualsAndHashCode(callSuper = false)
public class AttendanceRecordDTO {
    @ApiModelProperty(value = "签到id")
    private Long id;
    @ApiModelProperty(value = "学号")
    private String studentNum;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "课程")
    private String courseName;
    @ApiModelProperty(value = "老师")
    private String teacherName;
    @ApiModelProperty(value = "课程时间")
    private String time;
    @ApiModelProperty(value = "教室")
    private String classRoomName;
    @ApiModelProperty(value = "考勤状态")
    private String type;
    @ApiModelProperty(value = "签到时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signTime;
    @ApiModelProperty(value = "备注")
    private String distance;
    @ApiModelProperty(value = "是否进行了修改")
    private boolean recordNumber;

}
