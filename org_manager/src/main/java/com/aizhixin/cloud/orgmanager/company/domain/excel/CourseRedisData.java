package com.aizhixin.cloud.orgmanager.company.domain.excel;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="课程详情excel导入信息")
@NoArgsConstructor
@ToString
public class CourseRedisData implements Serializable {
    @ApiModelProperty(value = "state 处理中10，成功20，失败30", position=1)
    @Getter @Setter private Integer state;
    @ApiModelProperty(value = "message 成功失败描述消息", position=2)
    @Getter @Setter private String message;
    @ApiModelProperty(value = "courseExcelDomains 课程列表", position=3)
    @Getter @Setter private List<CourseExcelDomain> courseExcelDomains;
}
