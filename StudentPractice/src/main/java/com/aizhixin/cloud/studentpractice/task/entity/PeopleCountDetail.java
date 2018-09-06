
package com.aizhixin.cloud.studentpractice.task.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 班级实践详情表
 * @author zhengning
 *
 */
@Entity(name = "SP_PEOPLE_COUNT_DETAIL")
@ToString
public class PeopleCountDetail  {
	
	@ApiModelProperty(value = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter @Setter private Long id;

	@ApiModelProperty(value = "学号")
	@Column(name = "JOB_NUM")
	@Getter @Setter private String jobNum;
	
	@ApiModelProperty(value = "学生id")
	@Column(name = "STUDENT_ID")
	@Getter @Setter private Long studentId;
	
	@ApiModelProperty(value = "学生名称")
	@Column(name = "STUDENT_NAME")
	@Getter @Setter private String studentName;
	
	@ApiModelProperty(value = "学生性别")
	@Column(name = "STUDENT_SEX")
	@Getter @Setter private String studentSex;
	
	@ApiModelProperty(value = "学生手机号")
	@Column(name = "STUDENT_PHONE")
	@Getter @Setter private String studentPhone;
	
	@ApiModelProperty(value = "实践企业id")
	@Column(name = "ENTERPRISE_ID")
	@Getter @Setter private Long enterpriseId;
	
	@ApiModelProperty(value = "实践企业名称")
	@Column(name = "ENTERPRISE_NAME")
	@Getter @Setter private String enterpriseName;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "学生id")
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	
	@ApiModelProperty(value = "导师名称")
	@Column(name = "MENTOR_NAME")
	@Getter @Setter private String mentorName;
	
	@ApiModelProperty(value = "导师手机号")
	@Column(name = "MENTOR_PHONE")
	@Getter @Setter private String mentorPhone;
	
	@ApiModelProperty(value = "班级id")
	@Column(name = "CLASS_ID")
	@Getter @Setter private Long classId;
	
	@ApiModelProperty(value = "院系id")
	@Column(name = "COLLEGE_ID")
	@Getter @Setter private Long collegeId;
	
	@ApiModelProperty(value = "专业id")
	@Column(name = "PROFESSIONAL_ID")
	@Getter @Setter private Long professionalId;
	
	@ApiModelProperty(value = "实践公司所在省份")
	@Column(name = "PROVINCE")
	@Getter @Setter private String province;
	
	@ApiModelProperty(value = "实践公司所在城市")
	@Column(name = "CITY")
	@Getter @Setter private String city;
	
	@ApiModelProperty(value = "是否实践")
	@Column(name = "WHETHER_PRACTICE")
	@Getter @Setter private String whetherPractice;
	
	@ApiModelProperty(value = "班级名称")
	@Column(name = "CLASS_NAME")
	@Getter @Setter private String className;
	
	@ApiModelProperty(value = "专业名称")
	@Column(name = "PROFESSIONAL_NAME")
	@Getter @Setter private String professionalName;
	
	@ApiModelProperty(value = "学院名称")
	@Column(name = "COLLEGE_NAME")
	@Getter @Setter private String collegeName;
	
	@ApiModelProperty(value = "年级")
	@Column(name = "GRADE")
	@Getter @Setter private String grade;
	
	@ApiModelProperty(value = "提交日报数量")
	@Column(name = "DAILY_NUM")
	@Getter @Setter private Integer dailyNum;
	
	@ApiModelProperty(value = "提交周报数量")
	@Column(name = "WEEKLY_NUM")
	@Getter @Setter private Integer weeklyNum;
	
	@ApiModelProperty(value = "提交月报数量")
	@Column(name = "MONTHLY_NUM")
	@Getter @Setter private Integer monthlyNum;
	
	@ApiModelProperty(value = "提交日志周志总数")
	@Column(name = "SUMMARY_TOTAL_NUM")
	@Getter @Setter private Integer summaryTotalNum;
	
	@ApiModelProperty(value = "实践报告状态:未提交[uncommit],待审核[checkPending],已通过[pass],未通过[notPass],被打回[backTo]")
	@Column(name = "REPORT_STATUS")
	@Getter @Setter private String reportStatus;
	
	@ApiModelProperty(value = "备注")
	@Column(name = "REMARK")
	@Getter @Setter protected String remark;
	
	@ApiModelProperty(value = "辅导员id")
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter protected Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称")
	@Column(name = "COUNSELOR_NAME")
	@Getter @Setter protected String counselorName;
	
	@ApiModelProperty(value = "是否提交过日志周志或实践任务")
	@Column(name = "WHETHER_COMMIT")
	@Getter @Setter protected String whetherCommit;
	
	@ApiModelProperty(value = "实践签到总数")
	@Column(name = "SIGNIN_TOTAL_NUM")
	@Getter @Setter private Integer signInTotalNum;
	
	@ApiModelProperty(value = "正常签到数")
	@Column(name = "SIGNIN_NORMAL_NUM")
	@Getter @Setter private Integer signInNormalNum;
	
	@ApiModelProperty(value = "请假数")
	@Column(name = "LEAVE_NUM")
	@Getter @Setter private Integer leaveNum;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
	@ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter private Date createdDate = new Date();


	@ApiModelProperty(value = "最后一次修改时间")
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter private Date lastModifiedDate = new Date();

	@ApiModelProperty(value = "是否删除标志")
	@Column(name = "DELETE_FLAG")
	@Getter @Setter private Integer deleteFlag = DataValidity.VALID.getIntValue();
}
