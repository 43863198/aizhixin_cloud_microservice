package com.aizhixin.cloud.dd.classperf.dto;

import com.aizhixin.cloud.dd.classperf.entity.ClassPerfLogFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ClassPerfBatchDTO {
    private Long teacherId;
    private Long studentId;
    private String comment;
    private Integer score;
    @ApiModelProperty(value = "10:加分 20:减分")
    private Integer type;
    private List<ClassPerfLogFile> files;

}
