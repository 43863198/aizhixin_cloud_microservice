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
 * 学周实体对象
 * @author zhen.pan
 *
 */
@Entity(name = "T_WEEK")
@ToString
public class Week extends AbstractEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	/*
	 * 学周名称
	 */
	@Column(name = "NAME")
	@Getter @Setter private String name;
	/*
	 * 学校
	 */
	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	/*
	 * 学期
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEMESTER_ID")
	@Getter @Setter private Semester semester;
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
	 * 周序号，第几周
	 */
	@Column(name = "NO")
	@Getter @Setter private  Integer no;
}
