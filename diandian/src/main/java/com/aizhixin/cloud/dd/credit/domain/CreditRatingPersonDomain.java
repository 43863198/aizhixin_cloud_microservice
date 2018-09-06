package com.aizhixin.cloud.dd.credit.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "评分学生")
@ToString
@Data
public class CreditRatingPersonDomain {
    @ApiModelProperty(value = "id")
    protected Long id;
    @ApiModelProperty(value = "creditId")
    private Long creditId;
    @ApiModelProperty(value = "stuId")
    private Long stuId;
    @ApiModelProperty(value = "stuName")
    private String stuName;
    @ApiModelProperty(value = "userId")
    private Long userId;
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "avatar")
    private String avatar;
    @ApiModelProperty(value = "classesId")
    private Long classesId;
    @ApiModelProperty(value = "classesName")
    private String classesName;
    @ApiModelProperty(value = "collegeId")
    private Long collegeId;
    @ApiModelProperty(value = "collegeName")
    private String collegeName;
    @ApiModelProperty(value = "profId")
    private Long profId;
    @ApiModelProperty(value = "profName")
    private String profName;
    @ApiModelProperty(value = "orgId")
    private Long orgId;
    @ApiModelProperty(value = "orgName")
    private String orgName;
    @ApiModelProperty(value = "userType")
    private Integer userType;
    @ApiModelProperty(value = "classId")
    private Long classId;
}
