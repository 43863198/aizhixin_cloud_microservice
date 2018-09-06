package com.aizhixin.cloud.orgmanager.classschedule.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="批量修改教学班的课程数据")
@ToString
@NoArgsConstructor
public class TeachingclassBatchUpdateCourseDomain extends TeachingclassIdsAndOrgAndUserDomain {
    @ApiModelProperty(value = "课程ID", required = true)
    @Getter @Setter protected Long courseId;
}
