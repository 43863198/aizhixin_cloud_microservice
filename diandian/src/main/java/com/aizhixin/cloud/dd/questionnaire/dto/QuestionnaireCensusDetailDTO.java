/**
 *
 */
package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssginStudents;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 问卷统计详情
 *
 * @author meihua.li
 */
@Data
@ApiModel
public class QuestionnaireCensusDetailDTO {
    @ApiModelProperty("问卷id")
    private Long questionnaireId;
    @ApiModelProperty("问卷名称")
    private String questionnaireName;
    @ApiModelProperty("问卷分配id")
    private Long questionnaireAssginId;
    @ApiModelProperty("教师")
    private String teacherName;
    @ApiModelProperty("课程名称")
    private String courseName;
    @ApiModelProperty("课程编码")
    private String couresCode;
    @ApiModelProperty("班级名称集合")
    private List<String> classNames;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("班级id")
    private Long classId;

    private int totalCount;
    private int totalScore;

    private int commitCount;

    private float averageScore;

    private String leaveName;
    private Page<QuestionnaireAssginStudents> page;

    private List<QuestionnaireAssginStudents> questionnaireAssginStudents;

}
