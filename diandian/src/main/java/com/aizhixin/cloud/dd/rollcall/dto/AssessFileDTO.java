package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "评论文件")
@Data
public class AssessFileDTO {
    private String fileName;
    private String fileSrc;
    private String type;
    private Long fileSize;
}
