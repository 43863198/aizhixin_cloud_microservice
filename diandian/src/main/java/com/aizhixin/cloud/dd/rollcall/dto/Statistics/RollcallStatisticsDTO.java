package com.aizhixin.cloud.dd.rollcall.dto.Statistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-11-02
 */
@Data
@ApiModel(description = "辅导员点名统计")
public class RollcallStatisticsDTO {
    @ApiModelProperty(value = "点名id")
    private Long rid;
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
    @ApiModelProperty(value = "行政班级id")
    private Long classId;
    @ApiModelProperty(value = "行政班级")
    private String className;
    @ApiModelProperty(value = "年级")
    private String grade;
    @ApiModelProperty(value = "行政班学院")
    private String sCollegeName;
    @ApiModelProperty(value = "总人数")
    private int total;
    @ApiModelProperty(value = "已提交人数")
    private int submitted;
    @ApiModelProperty(value = "未提交人数")
    private int uncommitted;
    @ApiModelProperty(value = "请假人数")
    private int leave;
}
