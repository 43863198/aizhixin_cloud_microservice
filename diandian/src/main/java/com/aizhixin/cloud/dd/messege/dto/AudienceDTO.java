package com.aizhixin.cloud.dd.messege.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AudienceDTO {

    @ApiModelProperty(value="用户id")
    private Long userId;

    @ApiModelProperty(value = "cid")
    private String cid;

    @ApiModelProperty(value = "业务数据")
    private Object data;

    public AudienceDTO() {
    }

    public AudienceDTO(Long userId) {
        this.userId = userId;
    }

    public AudienceDTO(Long userId, Object data) {
        this.userId = userId;
        this.data = data;
    }

    public AudienceDTO(Long userId, String cid, Object data) {
        this.userId = userId;
        this.cid = cid;
        this.data = data;
    }
}
