package com.aizhixin.cloud.dd.counsellorollcall.domain;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by LIMH on 2017/11/30.
 */
@ApiModel(description = "辅导员点名组")
@ToString
@Data
public class CouRollCallDomain {
    private Long id;
    @ApiModelProperty(value = "开启时间", dataType = "String", notes = "开启时间")
    private String time;
    @ApiModelProperty(value = "组人数", dataType = "Long", notes = "组人数")
    private Long signInNum;
    @ApiModelProperty(value = "总人数", dataType = "Long", notes = "总人数")
    private Long total;
    @ApiModelProperty(value = "状态", dataType = "Boolean", notes = "状态")
    private Boolean status;

    public CouRollCallDomain() {}

    public CouRollCallDomain(Long id, Date time, Long signInNum, Long total, Boolean status) {
        this.id = id;
        this.time = (time == null ? null : DateFormatUtil.format(time));
        this.signInNum = signInNum;
        this.total = total;
        this.status = status;
    }
}
