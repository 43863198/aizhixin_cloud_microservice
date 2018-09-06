package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExcellentCourseApplyDomainV2 {
    @ApiModelProperty(value = "教师申请id")
    private String id;
    @ApiModelProperty(value = "审批状态 20：通过，30：拒绝")
    private Integer state;
}
