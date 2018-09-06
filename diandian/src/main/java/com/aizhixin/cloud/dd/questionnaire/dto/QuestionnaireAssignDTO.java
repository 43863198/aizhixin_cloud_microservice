/**
 *
 */
package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.rollcall.dto.TeachingClassesDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author meihua.li
 */
@ApiModel
@Data
public class QuestionnaireAssignDTO {
    @ApiModelProperty(value="问卷id")
    private Long QuestionnaireId;

    @ApiModelProperty(value="教学班集合")
    private List<TeachingClassesDTO> teachingClasses;

    @ApiModelProperty(value="行政班id集合")
    private List<Long> classesIds=new ArrayList<>();
    
    @ApiModelProperty(value="专业id集合")
    private List<Long> profIds=new ArrayList<>();
    
    @ApiModelProperty(value="学院id集合")
    private List<Long> collegeIds=new ArrayList<>();
    
    @ApiModelProperty(value="学校id集合")
    private Long orgId;
    
    @ApiModelProperty(value="分配类型，10：教学班，20：行政班，30：专业，40：学院，50：学校")
    private Integer assignType;
}
