package com.aizhixin.cloud.dd.credit.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ApiModel(description = "学分")
@ToString
@Data
public class CreditDomain {
    @ApiModelProperty(value = "id")
    protected Long id;
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "orgId")
    private Long orgId;
    @ApiModelProperty(value = "teacherId")
    private Long teacherId;
    @ApiModelProperty(value = "teacherName")
    private String teacherName;
    @ApiModelProperty(value = "templetId")
    private Long templetId;
    @ApiModelProperty(value = "templetName")
    private String templetName;
    @ApiModelProperty(value = "classCount")
    private Integer classCount;
    @ApiModelProperty(value = "ratingStuCount")
    private Integer ratingStuCount;
    @ApiModelProperty(value = "ratingPersonList")
    private List<CreditRatingPersonDomain> ratingPersonList;
}
