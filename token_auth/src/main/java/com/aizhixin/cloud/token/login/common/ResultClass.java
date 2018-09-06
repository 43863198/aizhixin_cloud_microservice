package com.aizhixin.cloud.token.login.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Created by wu on 2017/7/20.
 */
@ApiModel(description="扫码返回信息")
@Data
public class ResultClass {
    @NotNull
    @ApiModelProperty(value = "返回信息代码", required = true)
    protected Integer resultCode;
    @NotNull
    @ApiModelProperty(value = "是否成功", required = true)
    protected Boolean success;
    @NotNull
    @ApiModelProperty(value = "信息", required = true)
    protected String message;


}
