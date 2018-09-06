package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@ApiModel(description="行号、编码、名称基础信息")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LineCodeNameBaseDomain implements Serializable {

    @ApiModelProperty(value = "line 行号", position=1)
    @Getter @Setter protected Integer line;
    @ApiModelProperty(value = "code 编码（编号）", position=2)
    @Getter @Setter protected String code;
    @ApiModelProperty(value = "name 名称", position=3)
    @Getter @Setter protected String name;
    @ApiModelProperty(value = "id ID", position=9)
    @Getter @Setter protected Long id;
    @ApiModelProperty(value = "msg 消息获取错误描述", position=10)
    @Getter @Setter protected String msg;

    public LineCodeNameBaseDomain(Integer line, String code, String name) {
        this.line = line;
        this.code = code;
        this.name = name;
    }
}