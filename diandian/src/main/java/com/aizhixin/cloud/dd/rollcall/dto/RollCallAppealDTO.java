package com.aizhixin.cloud.dd.rollcall.dto;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallAppealFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RollCallAppealDTO {
    @ApiModelProperty(value="排课id",required=true)
    private Long scheduleId;
    @ApiModelProperty(value="申诉内容",required=false)
    private String content;
    @ApiModelProperty(value="申诉图片",required=false)
    private List<RollCallAppealFile> appealFiles;
}
