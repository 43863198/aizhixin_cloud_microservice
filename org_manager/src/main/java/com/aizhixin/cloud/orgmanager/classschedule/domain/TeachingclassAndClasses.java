package com.aizhixin.cloud.orgmanager.classschedule.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "班级信息")
@NoArgsConstructor
public class TeachingclassAndClasses {
    @ApiModelProperty(value = "教学班ID")
    private  Long teachingclassId;
    @ApiModelProperty(value = "教学班名称")
    private  String teachingclassName;
    @ApiModelProperty(value = "行政班ID")
    private  Long classesId;
    @ApiModelProperty(value = "行政班ID")
    private  String classesName;

    public TeachingclassAndClasses(Long teachingclassId, String teachingclassName, Long classesId, String classesName) {
        this.teachingclassId = teachingclassId;
        this.teachingclassName = teachingclassName;
        this.classesId = classesId;
        this.classesName = classesName;
    }
}
