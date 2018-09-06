package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "问卷评教 分题统计")
@Data
@EqualsAndHashCode(callSuper = false)
public class PartStatisticsDTO {
    private Long questionId;
    private Integer no;
    private String questionName;
    private Integer score;
    private Integer count;
    private String ration;//比例
}
