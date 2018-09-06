package com.aizhixin.cloud.dd.appeal.dto;

import com.aizhixin.cloud.dd.appeal.entity.AppealFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppealDTO {

    @ApiModelProperty(value = "申诉内容", required = false)
    private String content;

    @ApiModelProperty(value = "申诉图片", required = false)
    private List<AppealFile> appealFiles;

    @ApiModelProperty(value = "功能类型 10: 点名申诉", required = false)
    private Integer type;

    @ApiModelProperty(value = "点名申诉数据", required = false)
    private CounsellorDTO counsellorDTO;
}
