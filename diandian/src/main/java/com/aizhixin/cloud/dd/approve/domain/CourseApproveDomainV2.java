package com.aizhixin.cloud.dd.approve.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CourseApproveDomainV2 {
    @ApiModelProperty(value = "主键id")
    private Long id;
    @ApiModelProperty(value = "申请人id")
    private Long applyUserId;
    @ApiModelProperty(value = "申请人名称")
    private String applyUserName;
    @ApiModelProperty(value = "申请人头像")
    private String applyUserAvtar;
    @ApiModelProperty(value = "审批编号")
    private String approveNum;
    @ApiModelProperty(value = "申请类型，examination_approval:调停课审批")
    private String approveType;
    @ApiModelProperty(value = "审批状态")
    private  Integer approveState;
    @ApiModelProperty(value = "详情内容")
    private String context;
    @ApiModelProperty(value = "审批人")
    private Long approveUserId;
    @ApiModelProperty(value = "审批人名称")
    private String approveUserName;
    @ApiModelProperty(value = "审批意见")
    private String approveOpinion;
    @ApiModelProperty(value = "审批时间")
    private Date approveDate;
    @ApiModelProperty(value = "审批图片地址")
    private List<String> approveImgLsit=new ArrayList<>();
    @ApiModelProperty(value = "审批结果图片集")
    private List<String>  approveImgResultList=new ArrayList<>();
}
