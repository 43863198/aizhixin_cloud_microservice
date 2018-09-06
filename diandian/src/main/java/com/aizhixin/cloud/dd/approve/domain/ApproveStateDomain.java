package com.aizhixin.cloud.dd.approve.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApproveStateDomain {
    @ApiModelProperty(value = "审批id")
    private Long courseApproveId;
    @ApiModelProperty(value = "审批状态,20：拒绝,30：通过,40：撤销")
    private Integer approveState;
    @ApiModelProperty(value = "审批意见,撤销：不用填")
    private String approveOpinion;
    @ApiModelProperty(value = "审批结果图片集")
    private List<String>  approveImgResultList=new ArrayList<>();
}
