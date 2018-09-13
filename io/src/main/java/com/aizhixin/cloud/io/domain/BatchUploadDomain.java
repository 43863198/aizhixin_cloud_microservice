package com.aizhixin.cloud.io.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zhen.pan on 2017/6/13.
 */
@ApiModel(description="批量上传文件信息")
@ToString
@NoArgsConstructor
public class BatchUploadDomain implements Serializable {
    @ApiModelProperty(value = "上传文件对象")
    @Getter @Setter private MultipartFile file;
    @ApiModelProperty(value = "文件名称")
    @Getter @Setter private String filename;
}
