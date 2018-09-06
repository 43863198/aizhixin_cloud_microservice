package com.aizhixin.cloud.ew.live.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by DuanWei on 2017/6/5.
 */
@ApiModel(value = "LiveCommentDomain", description = "WEB点播数据的结构体")
@Data
public class LiveCommentDomain {
    @ApiModelProperty(value = "ID", required = false)
    private Long id;
    @ApiModelProperty(value = "视频ID(必填)", required = true)
    private Long videoId;
    @ApiModelProperty(value = "评论人姓名", required = false)
    private String name;
    @ApiModelProperty(value = "评论人ID", required = false)
    private Long userId;
    @ApiModelProperty(value = "评论内容(必填)", required = true)
    private String text;
    @ApiModelProperty(value = "评论时间", required = false)
    private Date commentTime;
}
