package com.aizhixin.cloud.dd.counsellorollcall.domain;

import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcallRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by LIMH on 2017/11/29.
 */
@ApiModel(description = "辅导员点名组")
@ToString
@Data
public class TempGroupDomainV2 {
    private Long id;
    @ApiModelProperty(value = "组名称", dataType = "String", notes = "组名称")
    private String name;
    @ApiModelProperty(value = "组人数", dataType = "String", notes = "组人数")
    private Integer subGroupNum;
    @ApiModelProperty(value = "点名次数")
    private Integer rollcallNum;
    @ApiModelProperty(value = "点名类型")
    private Integer rollcallType;
    @ApiModelProperty(value = "点名规则id")
    private Long ruleId;
    @ApiModelProperty(value = "当前组点名状态", dataType = "Boolean", notes = "当前组点名状态")
    private Boolean status;

    @ApiModelProperty(value = "1次打卡：打卡时间  2次打卡：打卡时间段(仅显示用)")
    private String alarmTime;
    @ApiModelProperty(value = "周几")
    private String alarmModel;
    @ApiModelProperty(value = "点名规则")
    private CounsellorRollcallRule rule;

    @ApiModelProperty(value = "辅导员名称", notes = "辅导员名称")
    private String teacherName;

    private String messageId;

    public TempGroupDomainV2() {
    }

    public TempGroupDomainV2(Long id, String name, Integer subGroupNum, Integer rollcallNum, Boolean status, Integer rollcallType, Long ruleId, String messageId) {
        this.id = id;
        this.name = name;
        this.subGroupNum = subGroupNum;
        this.status = status;
        this.rollcallNum = rollcallNum;
        this.rollcallType = rollcallType;
        this.ruleId = ruleId;
        this.messageId = messageId;
    }

    public TempGroupDomainV2(Long id, String name, Integer subGroupNum, Integer rollcallNum, Boolean status, Integer rollcallType, Long ruleId, String messageId, String teacherName) {
        this.id = id;
        this.name = name;
        this.subGroupNum = subGroupNum;
        this.status = status;
        this.rollcallNum = rollcallNum;
        this.rollcallType = rollcallType;
        this.ruleId = ruleId;
        this.messageId = messageId;
        this.teacherName = teacherName;
    }
}
