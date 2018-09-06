package com.aizhixin.cloud.dd.rollcall.dto.Attendance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-21
 */
@Data
public class AttendanceRateDTO implements Comparable<AttendanceRateDTO>{

    @ApiModelProperty(value = "教师姓名")
    private String teacherName;
    @ApiModelProperty(value = "学生签到率")
    private Double attendanceRate;
    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @Override
    public int compareTo(AttendanceRateDTO o) {
        if (this.attendanceRate > o.attendanceRate) {
            return -1;//由高到底排序
        }else if (this.attendanceRate < o.attendanceRate){
            return 1;
        }else{
            return 0;
        }
    }
}
