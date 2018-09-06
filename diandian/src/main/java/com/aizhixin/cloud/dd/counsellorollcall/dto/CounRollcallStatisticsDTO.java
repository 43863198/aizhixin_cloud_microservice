package com.aizhixin.cloud.dd.counsellorollcall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author LIMH
 * @date 2017/12/19
 */
@Data
@ApiModel(description = "导员点名web端列表")
public class CounRollcallStatisticsDTO {
    @ApiModelProperty(value = "点名id")
    private Long rid;
    @ApiModelProperty(value = "辅导员Id")
    private Long tId;
    @ApiModelProperty(value = "辅导员")
    private String tName;
    @ApiModelProperty(value = "工号")
    private String jobNumber;
    @ApiModelProperty(value = "辅导员归属学院")
    private String tCollegeName;
    @ApiModelProperty(value = "点名发起时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date initiatingTime;
    @ApiModelProperty(value = "年级")
    private String grade;
    @ApiModelProperty(value = "总人数")
    private int total;
    @ApiModelProperty(value = "已到人数")
    private int haveTo;
    @ApiModelProperty(value = "未提交人数")
    private int uncommitted;
    @ApiModelProperty(value = "未到人数")
    private int nonArrival;
    @ApiModelProperty(value = "请假人数")
    private int leave;
    @ApiModelProperty(value = "组名称")
    private String groupName;
}
