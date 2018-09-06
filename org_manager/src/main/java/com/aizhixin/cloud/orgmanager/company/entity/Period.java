/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 课程节实体对象
 * @author 郑宁
 *
 */
@Entity(name = "T_PERIOD")
@ToString
public class Period extends AbstractEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	/*
	 * 学校
	 */
	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	/*
	 *起始时间 
	 */
	@NotNull
    @Column(name = "START_TIME")
	@Getter @Setter private String startTime;
	/*
	 *终止时间
	 */
	@NotNull
    @Column(name = "END_TIME")
	@Getter @Setter private String endTime;
	/*
	 * 课程节序号，第几节
	 */
	@Column(name = "NO")
	@Getter @Setter private  Integer no;
}
