package com.aizhixin.cloud.dd.appeal.domain;

import com.aizhixin.cloud.dd.appeal.entity.AppealFile;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallAppealFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class AppealDomain {
    @ApiModelProperty(value = "申诉ID")
    private Long id;

    @ApiModelProperty(value = "申请人id")
    private Long applicantId;

    @ApiModelProperty(value = "申请人姓名")
    private String applicantName;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "班级")
    private String className;

    @ApiModelProperty(value = "审批人ID")
    private Long inspectorId;

    @ApiModelProperty(value = "审批人姓名")
    private String inspectorName;

    @ApiModelProperty(value = "功能类型")
    private Integer type;

    @ApiModelProperty(value = "申诉内容")
    private String content;

    @ApiModelProperty(value = "图片")
    private List<AppealFile> appealFiles;

    @ApiModelProperty(value = "申诉源数据")
    private DataDomain data;

    @ApiModelProperty(value = "审批状态 10:未审批 20:通过 30:不通过")
    private Integer appealStatus;

    @ApiModelProperty(value = "审批日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appealDate;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    public AppealDomain() {
    }
}
