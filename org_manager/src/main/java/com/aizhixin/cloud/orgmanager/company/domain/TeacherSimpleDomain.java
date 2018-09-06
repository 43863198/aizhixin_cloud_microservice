package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

/**
 * Created by zhen.pan on 2017/4/25.
 */
@ApiModel(description="简单的老师信息")
public class TeacherSimpleDomain extends IdNameDomain {
    @ApiModelProperty(value = "工号", position=7)
    @Size(min = 0, max = 50)
    @Getter @Setter protected String jobNumber;

    @ApiModelProperty(value = "学院名称", position=7)
    @Size(min = 0, max = 150)
    @Getter @Setter protected String collegeName;

    public TeacherSimpleDomain() {}

    public TeacherSimpleDomain(Long id, String name, String jobNumber, String collegeName) {
        super(id, name);
        this.jobNumber = jobNumber;
        this.collegeName = collegeName;
    }
}
