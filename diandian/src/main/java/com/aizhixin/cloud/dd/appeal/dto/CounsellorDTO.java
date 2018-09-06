package com.aizhixin.cloud.dd.appeal.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CounsellorDTO {
    @ApiModelProperty(value = "签到id")
    private Long signInId;

    @ApiModelProperty(value = "1:第一次打卡 2:第二次打卡")
    private Integer times;

}
