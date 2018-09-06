package com.aizhixin.cloud.dd.rollcall.dto;

import com.aizhixin.cloud.dd.appeal.entity.AppealFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "请假")
@Data
public class LeaveDTO {

    @ApiModelProperty(value = "leaveType", required = false)
    private Integer leaveType;

    @ApiModelProperty(value = "startDate", required = false)
    private String startDate;

    @ApiModelProperty(value = "endDate", required = false)
    private String endDate;

    @ApiModelProperty(value = "content", required = false)
    private String content;

    @ApiModelProperty(value = "图片", required = false)
    private List<AppealFile> appealFiles;
}
