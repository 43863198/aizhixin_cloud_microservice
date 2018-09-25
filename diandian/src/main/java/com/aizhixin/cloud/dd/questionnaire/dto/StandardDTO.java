package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StandardDTO {

    private Long id;
    /**
     * 序号
     */
    @ApiModelProperty(value = "序号", required = false)
    private Integer no;

    /**
     * 等级名称
     */
    @ApiModelProperty(value = "等级名称", required = false)
    private String levelName;
    /**
     * 分数段小值
     */
    @ApiModelProperty(value = "分数段小值", required = false)
    private Integer mixScore;
    /**
     * 分数段大值
     */
    @ApiModelProperty(value = "分数段大值", required = false)
    private Integer maxScore;

    public StandardDTO() {
    }

    public StandardDTO(Long id, Integer no, String levelName, Integer mixScore, Integer maxScore) {
        super();
        this.id = id;
        this.no = no;
        this.levelName = levelName;
        this.mixScore = mixScore;
        this.maxScore = maxScore;
    }
}
