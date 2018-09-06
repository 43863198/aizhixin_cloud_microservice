package com.aizhixin.cloud.ew.live.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by DuanWei on 2017/6/5.
 */
@ApiModel(value = "LiveSubscriptionDomain", description = "WEB订阅数据的结构体")
@Data
public class LiveSubscriptionDomain {
    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "视频ID(必填)")
    private Long videoId;
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "发布时间")
    private Date publishTime;
    @ApiModelProperty(value = "类型ID")
    private Long typeId;
    @ApiModelProperty(value = "开启状态")
    private String status;
    @ApiModelProperty(value = "订阅时间")
    private Date subscriptionTime;

}
