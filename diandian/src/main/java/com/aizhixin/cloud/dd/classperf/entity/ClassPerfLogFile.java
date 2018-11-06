package com.aizhixin.cloud.dd.classperf.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClassPerfLogFile {
    private String id;
    private String fileName;
    private String fileSrc;
    private String type;
    private Long fileSize;
}
