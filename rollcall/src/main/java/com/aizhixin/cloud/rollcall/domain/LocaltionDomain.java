package com.aizhixin.cloud.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by LIMH on 2017/11/21.
 */
@ApiModel(description = "签到位置信息")
@ToString
public class LocaltionDomain implements Serializable {
    @ApiModelProperty(value = "ID", position = 1)
    @Getter
    @Setter
    Long id;
    @ApiModelProperty(value = "lo", position = 2)
    @Getter
    @Setter
    String lo;
    @ApiModelProperty(value = "signTime", position = 3)
    @Getter
    @Setter
    private Date signTime;

    public LocaltionDomain() {}

    public LocaltionDomain(Long id, String lo, Date signTime) {
        this.id = id;
        this.lo = lo;
        this.signTime = signTime;
    }
}
