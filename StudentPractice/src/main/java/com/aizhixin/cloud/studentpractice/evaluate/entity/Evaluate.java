
package com.aizhixin.cloud.studentpractice.evaluate.entity;


import io.swagger.annotations.ApiModelProperty;
import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;



/**
 * 实践评价
 * @author zhengning
 *
 */
@Entity(name = "SP_EVALUATE")
@ToString
public class Evaluate extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	

	@ApiModelProperty(value = "机构id", required = false)
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "第一项评分(0-10分,两分为一颗星)", required = false)
	@Column(name = "FIRST_EVALUATE")
	@Getter @Setter private Integer firstEvaluate;
	
	@ApiModelProperty(value = "第二项评分(0-10分,两分为一颗星)", required = false)
	@Column(name = "SECOND_EVALUATE")
	@Getter @Setter private Integer secondEvaluate;
	
	@ApiModelProperty(value = "评价类型(s:学生自评,stc:学生对辅导员,stm:学生对导师,cts:辅导员对学生,mts:导师对学生)", required = false)
	@Column(name = "EVALUATE_TYPE")
	@Getter @Setter private String evaluateType;
	
	@ApiModelProperty(value = "评价建议", required = false)
	@Column(name = "ADVICE")
	@Getter @Setter private String advice;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	@Column(name = "COUNSELOR_NAME")
	@Getter @Setter private String counselorName;
	
	@ApiModelProperty(value = "辅导员学号")
	@Column(name = "COU_JOB_NUM")
	@Getter @Setter private String couJobNum;
	
	@ApiModelProperty(value = "学生id", required = false)
	@Column(name = "STUDENT_ID")
	@Getter @Setter private Long studentId;
	
	@ApiModelProperty(value = "学生名称", required = false)
	@Column(name = "STUDENT_NAME")
	@Getter @Setter private String studentName;
	
	@ApiModelProperty(value = "学生学号")
	@Column(name = "STU_JOB_NUM")
	@Getter @Setter private String stuJobNum;
	
	@ApiModelProperty(value = "导师id")
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	
	@ApiModelProperty(value = "导师名称")
	@Column(name = "MENTOR_NAME")
	@Getter @Setter private String mentorName;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
}
