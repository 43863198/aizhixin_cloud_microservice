package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "excel导入学校信息")
@NoArgsConstructor
@ToString
public class ImportBaseData implements Serializable {

    @ApiModelProperty(value = "state 处理中10，成功20，失败30", position = 1)
    @Getter
    @Setter
    private Integer state;

    @ApiModelProperty(value = "message 成功失败描述消息", position = 2)
    @Getter
    @Setter
    private String message;

    @ApiModelProperty(value = "班主任", position = 3)
    @Getter
    @Setter
    private List<ClassTeacherDomain> classTeacherDomainList;

    @ApiModelProperty(value = "教师", position = 4)
    @Getter
    @Setter
    private List<TeacherDomain> teacherDomainList;

    @ApiModelProperty(value = "学生", position = 5)
    @Getter
    @Setter
    private List<StudentDomain> studentDomainList;
}
