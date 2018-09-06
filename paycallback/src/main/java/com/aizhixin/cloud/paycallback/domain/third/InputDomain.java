package com.aizhixin.cloud.paycallback.domain.third;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="第三方调用入参")
@NoArgsConstructor
@ToString
public class InputDomain {
    @ApiModelProperty(value = "请求发起方的appid，由支付宝方生成并提供")
    @Getter
    @Setter
    private String id;

    @ApiModelProperty(value = "加密数据，加密处理方式为AES+base64编码，密钥(aes_key)由支付宝方生成该参数实际有效内容以JSON方式字符串")
    @Getter
    @Setter
    private String param;

    @ApiModelProperty(value = "签名，生成方式为对id+加密后的param+appsecret（由支付宝方提供）进行md5")
    @Getter
    @Setter
    private String sign;
}
