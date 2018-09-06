package com.aizhixin.cloud.orgmanager.classschedule.domain;


import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ApiModel(description="教学班课程老师列表查询输出对象")
@ToString
@NoArgsConstructor
public class TeachingclassCourseTeacherListDomain {
    @ApiModelProperty(value = "教学班ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter protected String name;
    @ApiModelProperty(value = "教学班编码")
    @Getter @Setter private String code;
    @ApiModelProperty(value = "课程ID")
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "教师列表")
    @Getter @Setter private List<IdNameDomain> teachers;
    public TeachingclassCourseTeacherListDomain (Long id, String name, String code, Long courseId, String courseName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.courseId = courseId;
        this.courseName = courseName;
    }
}
