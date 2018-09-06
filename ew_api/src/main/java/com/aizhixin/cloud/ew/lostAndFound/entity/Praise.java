package com.aizhixin.cloud.ew.lostAndFound.entity;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞或心痛表
 * @author Rigel.ma  2017-05-12
 *
 */
@Entity(name = "LF_PRAISE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class Praise extends AbstractOnlyIdEntity{
private static final long serialVersionUID = -5836009047318428476L;
	
	/**
	 * 失物招领信息ID
	 */	
	@Column(name = "LF_ID")
	private Long lfId;
	
	@CreatedBy
    @Column(name = "CREATED_BY")
    protected Long createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date createdDate = new Date();

 
    @CreatedDate
    @Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date lastModifiedDate = new Date();
    
    /**
	 * 点赞状态
	 */	
	@Column(name = "STATUS")
	private Integer status;

	
	
}
