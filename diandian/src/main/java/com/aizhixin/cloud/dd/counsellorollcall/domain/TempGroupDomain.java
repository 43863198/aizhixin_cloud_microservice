package com.aizhixin.cloud.dd.counsellorollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Created by LIMH on 2017/11/29.
 */
@ApiModel(description = "辅导员点名组")
@ToString
@Data
public class TempGroupDomain {
    private Long id;
    @ApiModelProperty(value = "组名称", dataType = "String", notes = "组名称")
    private String name;
    @ApiModelProperty(value = "组人数", dataType = "String", notes = "组人数")
    private Integer subGroupNum;
    @ApiModelProperty(value = "当前组点名状态", dataType = "Boolean", notes = "当前组点名状态")
    private Boolean status;
    private String alarmTime;
    private String alarmModel;

    public TempGroupDomain(Long id, String name, Integer subGroupNum, Boolean status) {
        this.id = id;
        this.name = name;
        this.subGroupNum = subGroupNum;
        this.status = status;
    }

    public TempGroupDomain(Long id, String name, Integer subGroupNum, Boolean status, String alarmTime, String alarmModel) {
        this.id = id;
        this.name = name;
        this.subGroupNum = subGroupNum;
        this.status = status;
        this.alarmTime = alarmTime;
        this.alarmModel = alarmModel;
    }
}
