package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="行号、id、msg信息")
@ToString
public class LineIdMsgDomain implements Serializable {
    @ApiModelProperty(value = "line 行号", position=1)
    @Getter @Setter protected Integer line;
    @ApiModelProperty(value = "id ID", position=2)
    @Getter @Setter protected Long id;
    @ApiModelProperty(value = "msg 消息获取错误描述", position=20)
    @Getter @Setter protected String msg;
}