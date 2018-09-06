package com.aizhixin.cloud.studentpractice.score.domain;

import java.util.Date;

import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description="实践参与计划设置")
@Data
public class TrainingGropSetDTO {
	@ApiModelProperty(value = "id")
	private Long id;
    @ApiModelProperty(value = "实践参与计划id")
    private Long groupId;
    @ApiModelProperty(value = "是否需要签到")
    private Boolean isNeedSign;
    @ApiModelProperty(value = "需要签到次数")
    private Integer needSignNum;
    @ApiModelProperty(value = "是否需要日志周志")
    private Boolean isNeedSummary;
    @ApiModelProperty(value = "需要日报数量")
    private Integer needDailyNum;
    @ApiModelProperty(value = "需要周报数量")
    private Integer needWeeklyNum;
    @ApiModelProperty(value = "需要月总结数量")
    private Integer needMonthlyNum;
    @ApiModelProperty(value = "是否需要实践报告")
    private Boolean isNeedReport;
    @ApiModelProperty(value = "签到所占权重")
    private Double signWeight;
    @ApiModelProperty(value = "周日志所占权重")
    private Double summaryWeight;
    @ApiModelProperty(value = "实践报告所占权重")
    private Double reportWeight;
    @ApiModelProperty(value = "实践任务所占权重")
    private Double taskWeight;
    @ApiModelProperty(value = "成绩生成日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date scoreDate;
}
