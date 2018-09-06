/**
 *
 */
package com.aizhixin.cloud.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhen.pan
 */
@ApiModel(description = "学生信息")
@NoArgsConstructor
@ToString
public class StudentDomain {
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private Long studentId;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private String studentName;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private String sutdentNum;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private Long classesId;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private String classesName;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private Long professionalId;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private Long collegeId;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private String teachingYear;
}
