package com.aizhixin.cloud.io.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by zhen.pan on 2017/6/13.
 */
@ApiModel(description="文件信息")
@ToString
@NoArgsConstructor
public class LocalFileDomain implements Serializable {
    @ApiModelProperty(value = "KEY")
    @Getter @Setter private String key;
    @ApiModelProperty(value = "文件名称")
    @Getter @Setter private String originalFileName;
    @ApiModelProperty(value = "文件扩展名称")
    @Getter @Setter private String extFileName;
    @ApiModelProperty(value = "文件相对路径")
    @Getter @Setter private String filePath;
    @ApiModelProperty(value = "文件有效期，暂无用")
    @Getter @Setter private Long ttl;
}
