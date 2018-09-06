package com.aizhixin.cloud.dd.counsellorollcall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author LIMH
 * @date 2017/12/19
 */
@Data
@ApiModel(description = "辅导员点名班级详情")
public class CounStudentDetail {

    @ApiModelProperty(value = "学生id")
    private Long id;
    @ApiModelProperty(value = "学号")
    private String jobNumber;
    @ApiModelProperty(value = "姓名")
    private String sName;
    @ApiModelProperty(value = "签到时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signTime;
    @ApiModelProperty(value = "状态")
    private String state;
    @ApiModelProperty(value = "是否阅读")
    private String isRead;
    @ApiModelProperty(value = "GPS坐标")
    private List<Double> lltudes;
    @ApiModelProperty(value = "地理位置")
    private String position;
}
