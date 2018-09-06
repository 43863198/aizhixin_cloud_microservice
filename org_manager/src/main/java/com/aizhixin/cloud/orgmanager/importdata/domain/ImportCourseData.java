package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "excel导入开课信息")
@NoArgsConstructor
@ToString
public class ImportCourseData implements Serializable {
    @ApiModelProperty(value = "state 处理中10，成功20，失败30", position = 1)
    @Getter
    @Setter
    private Integer state;

    @ApiModelProperty(value = "message 成功失败描述消息", position = 2)
    @Getter
    @Setter
    private String message;

    @ApiModelProperty(value = "教学班", position = 3)
    @Getter
    @Setter
    private List<TeachingClassDomain> teachingClassDomainList;

    @ApiModelProperty(value = "教学班学生", position = 4)
    @Getter
    @Setter
    private List<TeachingClassStudentDomain> teachingClassStudentDomainList;

    @ApiModelProperty(value = "课程表", position = 5)
    @Getter
    @Setter
    private List<ClassScheduleDomain> classScheduleDomainList;

}
