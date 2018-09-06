package com.aizhixin.cloud.orgmanager.classschedule.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class TeachingClassTeacherInfoDomain extends IdNameDomain {
    @ApiModelProperty(value = "logicId", required = false, allowableValues = "range[1,infinity]", position = 1)
    @Getter
    @Setter
    protected Long logicId;

    @ApiModelProperty(value = "collegeName", required = false)
    @Getter
    @Setter
    private String collegeName;

    public TeachingClassTeacherInfoDomain() {
    }

    public TeachingClassTeacherInfoDomain(Long id, String name, Long logicId, String collegeName) {
        super(id, name);
        this.logicId = logicId;
        this.collegeName = collegeName;
    }
}
