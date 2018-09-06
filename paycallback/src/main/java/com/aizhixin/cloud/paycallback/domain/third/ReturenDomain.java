package com.aizhixin.cloud.paycallback.domain.third;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@ApiModel(description="第三方调用返回")
@NoArgsConstructor
@ToString
public class ReturenDomain {
    @ApiModelProperty(value = "状态码")
    @Getter
    @Setter
    private String ret_code;
    @ApiModelProperty(value = "携带数据")
    @Getter
    @Setter
    private String ret_content;
    @ApiModelProperty(value = "签名")
    @Getter
    @Setter
    private String sign;

    public ReturenDomain (String ret_code, String ret_content, String key) {
        this.ret_code = ret_code;
        this.ret_content = ret_content;
        StringBuilder sb = new StringBuilder();
        sb.append(ret_code).append(ret_code).append(key);
        this.sign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }
}
