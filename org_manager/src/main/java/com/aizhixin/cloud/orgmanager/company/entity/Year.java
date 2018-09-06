/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import java.util.Date;

/**
 * 学年实体对象
 * @author 郑宁
 *
 */
@Entity(name = "T_YEAR")
@ToString
public class Year {
	private static final long serialVersionUID = -1728862780222167495L;
	
	@Id
	@Column(name = "ID")
	@Getter
	@Setter
	protected String id;

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
	
	@CreatedBy
    @Column(name = "CREATED_BY")
	@Getter
	@Setter
    protected Long createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
    protected Date createdDate = new Date();

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
	@Getter
	@Setter
    protected Long lastModifiedBy;

    @CreatedDate
    @Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
    protected Date lastModifiedDate = new Date();

	@Column(name = "DELETE_FLAG")
	@Getter
	@Setter
	protected Integer deleteFlag = DataValidity.VALID.getState();
}
