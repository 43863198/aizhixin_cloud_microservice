package com.aizhixin.cloud.ew.live.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * Created by DuanWei on 2017/6/16.
 */
@ApiModel(value = "PushMessageDto", description = "推送数据集")
@Data
public class PushMessageDto {
    private String title;
    private Long time;
    private List<Long> userId;
}
