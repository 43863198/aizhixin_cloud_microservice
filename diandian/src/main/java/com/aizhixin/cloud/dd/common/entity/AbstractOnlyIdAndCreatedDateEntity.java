package com.aizhixin.cloud.dd.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
@ToString
public abstract class AbstractOnlyIdAndCreatedDateEntity implements java.io.Serializable {
	private static final long serialVersionUID = -5481883489404960814L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter
	@Setter
	protected Long id;

	@CreatedDate
	@Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
	protected Date createdDate = new Date();

	@Column(name = "DELETE_FLAG")
	@Getter
	@Setter
	protected Integer deleteFlag = DataValidity.VALID.getState();
}
