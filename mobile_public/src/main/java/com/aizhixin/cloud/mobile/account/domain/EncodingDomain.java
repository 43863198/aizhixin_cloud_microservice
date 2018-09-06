package com.aizhixin.cloud.mobile.account.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "明文密文")
@NoArgsConstructor
@ToString
public class EncodingDomain {
    @ApiModelProperty(value = "明文")
    @Getter @Setter private String plainText;
    @ApiModelProperty(value = "密文")
    @Getter @Setter private String encodingText;

    public EncodingDomain (String plainText, String encodingText) {
        this.plainText = plainText;
        this.encodingText = encodingText;
    }
}
