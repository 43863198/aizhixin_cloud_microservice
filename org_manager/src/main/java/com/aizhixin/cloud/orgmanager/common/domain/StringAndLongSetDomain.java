package com.aizhixin.cloud.orgmanager.common.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@ApiModel(description="学校管理员信息")
@NoArgsConstructor
@ToString
public class StringAndLongSetDomain {
    @ApiModelProperty(value = "提示消息", position = 2)
    @Getter
    @Setter
    private String msg;
    @ApiModelProperty(value = "具体内容", position = 3)
    @Getter
    @Setter
    private Set<Long> ids;

    public StringAndLongSetDomain(String msg, Set<Long> ids) {
        this.msg = msg;
        this.ids = ids;
    }
}
