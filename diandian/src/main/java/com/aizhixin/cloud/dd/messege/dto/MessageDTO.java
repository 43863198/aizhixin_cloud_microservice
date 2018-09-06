package com.aizhixin.cloud.dd.messege.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MessageDTO {
    @ApiModelProperty(value="发送人和业务信息")
    private List<AudienceDTO> audience;

    @ApiModelProperty(value="标题")
    private String title;

    @ApiModelProperty(value="内容")
    private String content;

    @ApiModelProperty(value="消息类型 10:消息")
    private Integer type = 10;

    @ApiModelProperty(value="模块function")
    private String function;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel = false;

    @ApiModelProperty(value = "是否计数")
    private Boolean isCount = true;
}
