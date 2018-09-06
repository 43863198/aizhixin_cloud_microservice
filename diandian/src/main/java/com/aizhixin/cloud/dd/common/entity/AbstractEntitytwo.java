package com.aizhixin.cloud.dd.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
@ToString
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntitytwo implements java.io.Serializable {
	private static final long serialVersionUID = -2833890921053212135L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter
	@Setter
	protected Long id;

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

}
