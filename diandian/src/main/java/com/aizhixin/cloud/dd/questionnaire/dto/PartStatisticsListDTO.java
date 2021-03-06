package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@ApiModel(description = "问卷评教 分题统计")
@Data
@EqualsAndHashCode(callSuper = false)
public class PartStatisticsListDTO {
    private Integer no;
    private String questionName;
    private Integer totalCount = 0;
    private Float score = 0f;
    private Float totalScore = 0f;
    private String avg;
    private List<PartStatisticsDTO> data;
}
