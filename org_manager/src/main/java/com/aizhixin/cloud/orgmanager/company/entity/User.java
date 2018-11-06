/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.company.core.RollCallType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 用户实体对象
 * @author zhen.pan
 *
 */
@Entity(name = "T_USER")
@ToString
@EntityListeners(AuditingEntityListener.class)
public class User implements java.io.Serializable {
	private static final long serialVersionUID = -6024934429668082352L;

	@Id
	@Column(name = "ID")
	@Getter @Setter private Long id;

	@Column(name = "CREATED_BY")
	@Getter @Setter private Long createdBy;

	@CreatedDate
	@Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter @Setter private Date createdDate = new Date();

	@Column(name = "LAST_MODIFIED_BY")
	@Getter @Setter private Long lastModifiedBy;

	@LastModifiedDate
	@Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter @Setter private Date lastModifiedDate = new Date();

	@Column(name = "DELETE_FLAG")
	@Getter @Setter private Integer deleteFlag = DataValidity.VALID.getState();
	/**
	 * 对应登录账号的ID
	 */
    @Column(name = "ACCOUNT_ID")
    @Getter @Setter private Long accountId;
	/**
	 * 用户类型(10学校管理员，20学院管理员，40班级管理员，60老师，70学生)
	 */
    @Column(name = "USER_TYPE")
    @Getter @Setter private Integer userType;
    /**
     * 姓名
     */
	@Column(name = "NAME")
	@Getter @Setter private String name;
	/**
	 * 电话号码
	 */
    @Column(name = "PHONE")
    @Getter @Setter private String phone;
    /**
     * 邮箱
     */
    @Column(name = "EMAIL")
    @Getter @Setter private String email;
    /**
     * 学号或者工号
     */
	@Column(name = "JOB_NUMBER")
	@Getter @Setter private String jobNumber;
    /**
     * 性别(男性male|女性female)
     */
	@Column(name = "SEX")
	@Getter @Setter private String sex;
    /**
     * 班级
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLASSES_ID")
	@Getter @Setter private Classes classes;
	/**
	 * 专业
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROFESSIONAL_ID")
	@Getter @Setter private Professional professional;
	/**
	 * 学院
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COLLEGE_ID")
	@Getter @Setter private College college;
	/**
	 * 组织、学校
	 */
	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	/**
	 * 入学日期
	 */
	@Column(name = "IN_SCHOOL_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(TemporalType.DATE)
	@Getter @Setter private Date inSchoolDate = new Date();
	/**
	 * 在校、毕业(开除、休学等等不在上课)
	 */
	@Column(name = "SCHOOL_STATUS")
	@Getter @Setter private Integer schoolStatus;
	/**
	 * 登录名称
	 */
	@Column(name = "LOGIN_NAME")
	@Getter @Setter private String loginName;

	/**
	 * 如果有值表示不需要考勤，否则正常考勤
	 */
	@Column(name = "ROLLCALL")
	@Getter @Setter private Integer rollcall = RollCallType.NORMAL.getState();
	/**
	 * 身份证号
	 */
	@Column(name = "ID_NUMBER")
	@Getter @Setter private String idNumber;
	/**
	 * 录取通知书号
	 */
	@Column(name = "ADMISSION_NOTICE_NUMBER")
	@Getter @Setter private String admissionNoticeNumber;
	@ApiModelProperty(value = "生源地")
	@Column(name = "STUDENT_SOURCE")
	@Getter @Setter private String studentSource;
	@ApiModelProperty(value = "学生类别(单招、统招)")
	@Column(name = "STUDENT_TYPE")
	@Getter @Setter private String studentType;
	@ApiModelProperty(value = "层次(专科、本科)")
	@Column(name = "EDU_LEVEL")
	@Getter @Setter private String eduLevel;
	@ApiModelProperty(value = "年级")
	@Column(name = "GRADE")
	@Getter @Setter private String grade;
	@ApiModelProperty(value = "校区")
	@Column(name = "SCHOOL_LOCAL")
	@Getter @Setter private String schoolLocal;

	@Column(name = "is_choose_dormitory")
	@Getter @Setter private Boolean isChooseDormitory;

	@Column(name = "is_monitor")
	@Getter @Setter private Boolean isMonitor;
}