package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;


@ApiModel(description="教学班ID列表及组织机构和操作人")
@ToString
@NoArgsConstructor
public class TeachingclassIdsAndOrgAndUserDomain {
    @ApiModelProperty(value = "教学班ID列表", required = true)
    @Getter @Setter protected Set<Long> teachingclassIds;
    @ApiModelProperty(value = "组织机构ID", required = true)
    @Getter @Setter protected Long orgId;
    @ApiModelProperty(value = "操作人", required = true)
    @Getter @Setter protected Long userId;
}
