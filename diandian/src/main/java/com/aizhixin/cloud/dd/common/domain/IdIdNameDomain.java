package com.aizhixin.cloud.dd.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhen.pan on 2017/5/10.
 */
@ApiModel(description="业务逻辑ID加id和name信息")
@ToString
public class IdIdNameDomain extends IdNameDomain {
    @ApiModelProperty(value = "logicId", required = false, allowableValues = "range[1,infinity]", position=1)
    @Getter @Setter protected Long logicId;

    public IdIdNameDomain () {}

    public IdIdNameDomain(Long logicId, Long id, String name) {
        super(id, name);
        this.logicId = logicId;
    }
}
