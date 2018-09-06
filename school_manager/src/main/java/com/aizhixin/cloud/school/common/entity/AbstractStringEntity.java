package com.aizhixin.cloud.school.common.entity;

import com.aizhixin.cloud.school.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@ToString
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractStringEntity implements java.io.Serializable {
	private static final long serialVersionUID = -2833890921053212135L;
	@Id
	@Column(name = "ID")
	@Getter
	@Setter
	protected String id;

	@CreatedBy
    @Column(name = "CREATED_BY")
	@Getter
	@Setter
    protected Long createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
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
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
    protected Date lastModifiedDate = new Date();

	@Column(name = "DELETE_FLAG")
	@Getter
	@Setter
	protected Integer deleteFlag = DataValidity.VALID.getState();
}
