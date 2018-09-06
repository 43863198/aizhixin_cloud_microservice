package com.aizhixin.cloud.dd.appeal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class CounsellorDomain implements DataDomain {
    @ApiModelProperty(value = "签到id")
    private Long signInId;

    @ApiModelProperty(value = "1:第一次打卡 2:第二次打卡")
    private Integer times;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "点名组id")
    private Long groupId;

    @ApiModelProperty(value = "点名组名称")
    private String groupName;

    @ApiModelProperty(value = "点名组类型")
    private Integer rollcallType;

    @ApiModelProperty(value = "打卡开启时间")
    private Timestamp openTime;

    @ApiModelProperty(value = "打卡时间段")
    private String startEndTime;
}
