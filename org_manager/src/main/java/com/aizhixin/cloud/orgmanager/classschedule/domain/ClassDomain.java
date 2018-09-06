package com.aizhixin.cloud.orgmanager.classschedule.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;


@ApiModel(description="教学班下学生所在行政班信息")
public class ClassDomain extends IdNameDomain {

    @ApiModelProperty(value = "学期ID", required = true)
    @Getter @Setter private Long semesterId;
    
    @ApiModelProperty(value = "机构ID", required = true)
    @Getter @Setter private Long orgId;
}