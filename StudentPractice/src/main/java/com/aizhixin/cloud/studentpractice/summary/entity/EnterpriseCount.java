
package com.aizhixin.cloud.studentpractice.summary.entity;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 企业实践人数统计表
 * @author zhengning
 *
 */
@Entity(name = "SP_ENTERPRISE_COUNT")
@ToString
public class EnterpriseCount  {
	

	@ApiModelProperty(value = "ID")
	@Id
	@Column(name = "ID")
	@Getter @Setter private String id;

	@ApiModelProperty(value = "企业名称")
	@Column(name = "NAME")
	@Getter @Setter private String name;
	
	@ApiModelProperty(value = "所在省份")
	@Column(name = "PROVINCE")
	@Getter @Setter private String province;
	
	@ApiModelProperty(value = "所在市")
	@Column(name = "CITY")
	@Getter @Setter private String city;
	
	@ApiModelProperty(value = "所在县")
	@Column(name = "COUNTY")
	@Getter @Setter private String county;
	
	@ApiModelProperty(value = "企业地址")
	@Column(name = "ADDRESS")
	@Getter @Setter private String address;
	
	@ApiModelProperty(value = "联系方式")
	@Column(name = "TELEPHONE")
	@Getter @Setter private String telephone;
	
	@ApiModelProperty(value = "邮箱")
	@Column(name = "MAILBOX")
	@Getter @Setter private String mailbox;
	
	@ApiModelProperty(value = "学生实践人次")
	@Column(name = "STU_NUM")
	@Getter @Setter private Long stuNum;
	
	@ApiModelProperty(value = "企业导师人数")
	@Column(name = "MENTOR_NUM")
	@Getter @Setter private Long mentorNum;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter private Date createdDate = new Date();

	@ApiModelProperty(value = "是否删除标志")
	@Column(name = "DELETE_FLAG")
	@Getter @Setter private Integer deleteFlag = DataValidity.VALID.getIntValue();
}
