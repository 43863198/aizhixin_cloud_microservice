package com.aizhixin.cloud.orgmanager.company.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/7/6.
 */
@ApiModel(description="班级信息")
@Data
public class ClassesIdNameCollegeNameDomain {
    @ApiModelProperty(value = "班级ID", position=1)
    private Long classesId;
    @ApiModelProperty(value = "班级名称", position=2)
    private String classesName;
    @ApiModelProperty(value = "专业ID", position=3)
    private Long professionalId;
    @ApiModelProperty(value = "专业名称", position=4)
    private String professionalName;
    @ApiModelProperty(value = "学院ID", position=5)
    private Long collegeId;
    @ApiModelProperty(value = "学院名称", position=6)
    private String collegename;

    public ClassesIdNameCollegeNameDomain () {}

    public ClassesIdNameCollegeNameDomain(Long classesId, String classesName, Long professionalId, String professionalName, Long collegeId, String collegename) {
        this.classesId = classesId;
        this.classesName = classesName;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.collegeId = collegeId;
        this.collegename = collegename;
    }
}
