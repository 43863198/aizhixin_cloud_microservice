package com.aizhixin.cloud.token.login.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by wu on 2017/7/20.
 */
@ApiModel(description="给socket服务传递的信息")
@Data
public class PostData {
    @NotNull
    @ApiModelProperty(value = "socketId", required = true)
    protected String id;
    @NotNull
    @ApiModelProperty(value = "扫码：ready，登录成功：success", required = true)
    protected String  action;
    @NotNull
    @ApiModelProperty(value = "发送的数据", required = true)
    protected String data;

}
