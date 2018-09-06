/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 学生班级实体
 * @author zhen.pan
 *
 */
@Entity(name = "T_CLASSES")
@ToString
public class Classes extends AbstractEntity {
	private static final long serialVersionUID = -4138514236088577990L;

	@NotNull
	@Column(name = "NAME")
	@Getter @Setter private String name;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROFESSIONAL_ID")
	@Getter @Setter private Professional professional;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COLLEGE_ID")
	@Getter @Setter private College college;

	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	/**
	 * 班级编码
	 */
	//	@NotNull
	@Column(name = "CODE")
	@Getter @Setter private String code;
	/**
	 * 入学日期
	 */
	@Column(name = "IN_SCHOOL_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(TemporalType.DATE)
	@Getter @Setter private Date inSchoolDate;
	/**
	 * 毕业日期
	 */
	@Column(name = "OUT_SCHOOL_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(TemporalType.DATE)
	@Getter @Setter private Date outSchoolDate;
	/**
	 * 在校、毕业
	 */
	@Column(name = "SCHOOL_STATUS")
	@Getter @Setter private Integer schoolStatus;
	/**
	 * 学年(2016)
	 */
//	@NotNull
	@Column(name = "TEACHING_YEAR")
	@Getter @Setter private String teachingYear;

	/**
	 * 学制
	 */
	@Column(name = "SCHOOLING_LENGTH")
	@Getter @Setter private String schoolingLength;

}