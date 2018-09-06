package com.aizhixin.cloud.dd.approve.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseApproveDomain {
    @ApiModelProperty(value = "主键id")
    private Long id;
    @ApiModelProperty(value = "申请类型，10:加课，20：调课，30：停课")
    private String approveType;
    @ApiModelProperty(value = "详情内容")
    private String context;
    @ApiModelProperty(value = "审批人")
    private Long approveUserId;
    @ApiModelProperty(value = "审批图片地址")
    private List<String> approveImgLsit=new ArrayList<>();
}
