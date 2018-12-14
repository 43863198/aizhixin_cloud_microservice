package com.aizhixin.cloud.dd.credit.domain;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.credit.entity.Credit;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ApiModel(description = "学分班级")
@ToString
@Data
public class CreditClassDomain {
    protected Long id;
    protected Date createdDate = new Date();
    protected Integer deleteFlag = DataValidity.VALID.getState();
    private Credit credit;
    private Long classId;
    private String className;
    private Integer commitCount;
    private Date lastSubmittedTime;
    private Float avgScore;
    private Float avgScorePct;
    private Integer ratingCount;
}
