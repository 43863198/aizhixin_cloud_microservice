package com.aizhixin.cloud.dd.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author meihua.li
 * @ClassName: QuestionnaireDTO
 * @Description:
 * @date 2017年5月26日 下午2:31:29
 */
@Data
public class QuestionnaireDTO implements Serializable {
    @ApiModelProperty(value = "ID", required = true)
    protected Long id;

    @NotNull
    @ApiModelProperty(value = "问卷名称", required = true)
    protected String name;

    @NotNull
    @ApiModelProperty(value = "题目数量", required = true)
    protected Integer totalQuestions;

    @NotNull
    @ApiModelProperty(value = "问卷总分", required = true)
    protected Integer totalScore;

    @NotNull
    @ApiModelProperty(value = "问卷总分小数", required = true)
    protected Float totalScore2;

    @ApiModelProperty(value = "创建时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createDate;

    @ApiModelProperty(value = "截至时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date endDate;

    @ApiModelProperty(value = "是否量化", required = true)
    private boolean quantification;

    @ApiModelProperty(value = "是否是选择题", required = true)
    private boolean choiceQuestion;

    @ApiModelProperty(value = "是否分配")
    private Integer status;

    @ApiModelProperty(value = "是否截至(状态)")
    protected String isEnd;

    @ApiModelProperty(value = "分配教学班数量")
    private Integer teachingNum;

    @ApiModelProperty(value = "问卷类型")
    private Integer quesType;

    @NotNull
    @ApiModelProperty(value = "题目", required = true)
    protected List<QuestionDTO> questions;

    @ApiModelProperty(value = "是否评语")
    private boolean qcomment;

    private Integer allocationNum;

    public QuestionnaireDTO() {

    }
}
