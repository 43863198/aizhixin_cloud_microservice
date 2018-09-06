package com.aizhixin.cloud.ew.common;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description="执行成功、失败结果及错误信息")
@Data
public class RestResult {
    private String result;
    private String success;
    private Long id;
    private String msg;
    public RestResult () {}
    public RestResult(String result, String msg) {
        this.result = result;
        this.msg = msg;
    }
}
