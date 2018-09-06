package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdsDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by zhen.pan on 2017/4/19.
 */
@ApiModel(description="班级学生列表")
public class BatchUserDomain extends IdsDomain {

    @NotNull
    @ApiModelProperty(value = "班级ID", required = true)
    @Getter @Setter protected Long classesId;
}
