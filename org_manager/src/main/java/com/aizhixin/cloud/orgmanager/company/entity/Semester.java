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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 学期实体对象
 * @author zhen.pan
 *
 */
@Entity(name = "T_SEMESTER")
@ToString
public class Semester extends AbstractEntity {
	private static final long serialVersionUID = -1728862780222167495L;
	/*
	 * 学期名称
	 */
	@NotNull
	@Column(name = "NAME")
	@Getter @Setter private String name;
	/*
	 * 学校
	 */
	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
//	@NotNull
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "YEAR_ID")
//	@Getter @Setter private Year year;
	/*
	 *起始时间 
	 */
	@NotNull
    @Column(name = "START_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.DATE)
	@Getter @Setter private Date startDate;
	/*
	 *终止时间
	 */
	@NotNull
    @Column(name = "END_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.DATE)
	@Getter @Setter private Date endDate;
	/*
	 * 周数量
	 */
	@Column(name = "NUM_WEEK")
	@Getter @Setter private  Integer numWeek;
	//	@NotNull
	@Column(name = "CODE")
	@Getter @Setter private String code;
}
