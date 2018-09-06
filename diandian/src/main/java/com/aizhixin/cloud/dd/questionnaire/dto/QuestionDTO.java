package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "问题")
@Data
public class QuestionDTO {

    private static final long serialVersionUID = 1073324416002687849L;

    private Long id;
    /**
     * 题号
     */
    @ApiModelProperty(value = "题号", required = false)
    private Integer no;

    /**
     * 题目名称
     */
    @ApiModelProperty(value = "题目名称", required = false)
    private String name;
    /**
     * 题目分值
     */
    @ApiModelProperty(value = "题目分值", required = false)
    private Integer score = 0;

    /**
     * 选择题选项
     */
    @ApiModelProperty(value = "题目选项", required = false)
    private List<QuestionsChoiceDTO> questionChioce=new ArrayList<>();
    
    /**
     * 题目分值
     */
    @ApiModelProperty(value = "实际得分", required = false)
    private Integer actualScore = 0;

    /**
     * 学生答案
     */
    @ApiModelProperty(value = "学生答案", required = false)
    private String answer ;
    
    /**
     *是否是单选
     */
    @ApiModelProperty(value = "是否是单选", required = false)
    private boolean radio;
    
    public QuestionDTO() {
    }

    public QuestionDTO(Long id, Integer no, String name, Integer score, Integer actualScore) {
        super();
        this.id = id;
        this.no = no;
        this.name = name;
        this.score = score;
        this.actualScore = actualScore;
    }

}