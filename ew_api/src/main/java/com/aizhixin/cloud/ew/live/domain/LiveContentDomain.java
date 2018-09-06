package com.aizhixin.cloud.ew.live.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by DuanWei on 2017/6/5.
 */
@ApiModel(value = "LiveContentDomain", description = "WEB点播数据的结构体")
@Data
public class LiveContentDomain {
    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "视频标题(必填)")
    private String title;
    @ApiModelProperty(value = "直播人(必填)")
    private String name;
    @ApiModelProperty(value = "封面图(必填)")
    private String coverPic;
    @ApiModelProperty(value = "详情图片(必填)")
    private String childPic;
    @ApiModelProperty(value = "保利威视数据(必填)")
    private String data;
    @ApiModelProperty(value = "开启状态")
    private String status;
    @ApiModelProperty(value = "视频状态")
    private String LiveStatus;
    @ApiModelProperty(value = "直播时间")
    private Date publishTime;
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "视频类型")
    private Long typeId;
    @ApiModelProperty(value = "在线访问人数")
    private Long onlineNumber;
    @ApiModelProperty(value = "视频时间")
    private String videoTime;
    @ApiModelProperty(value = "当前用户订阅状态")
    private String SubscriptionStatus;

}
