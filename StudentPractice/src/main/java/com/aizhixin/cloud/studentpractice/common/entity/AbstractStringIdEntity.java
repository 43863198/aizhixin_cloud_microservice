package com.aizhixin.cloud.studentpractice.common.entity;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@ToString
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractStringIdEntity implements java.io.Serializable {
	private static final long serialVersionUID = -2833890921053212135L;

	@ApiModelProperty(value = "ID")
	@Id
	@Column(name = "ID")
	@Getter @Setter protected String id;

	@ApiModelProperty(value = "创建人")
	@CreatedBy
    @Column(name = "CREATED_BY")
	@Getter @Setter protected Long createdBy;

	@ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter protected Date createdDate = new Date();

	@ApiModelProperty(value = "最后一次修改人")
    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
	@Getter @Setter protected Long lastModifiedBy;

	@ApiModelProperty(value = "最后一次修改时间")
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter protected Date lastModifiedDate = new Date();

	@ApiModelProperty(value = "是否删除标志")
	@Column(name = "DELETE_FLAG")
	@Getter @Setter protected Integer deleteFlag = DataValidity.VALID.getIntValue();
}
