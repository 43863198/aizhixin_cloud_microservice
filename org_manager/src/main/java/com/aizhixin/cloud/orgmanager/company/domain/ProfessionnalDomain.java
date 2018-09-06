/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="专业信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class ProfessionnalDomain extends IdUserNameBase {
	@ApiModelProperty(value = "学院ID", required = true)
	@NotNull
	@Digits(fraction = 0, integer = 18)
	private Long collegeId;
	
	@ApiModelProperty(value = "学院名称")
	private String collegeName;

	@ApiModelProperty(value = "学院编码")
	private String collegeCode;

	@ApiModelProperty(value = "专业编码")
	private String code;

	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;

	@ApiModelProperty(value = "组织机构ID")
	private Long orgId;
	public ProfessionnalDomain() {}

	public ProfessionnalDomain(Long id, String name, Long collegeId, String collegeName) {
		super(id, name);
		this.collegeId = collegeId;
		this.collegeName = collegeName;
	}
	
	public ProfessionnalDomain(Long id, String code, String name, Long collegeId, String collegeName, Date createdDate) {
		this(id, name, collegeId, collegeName);
		this.code = code;
		this.createdDate = createdDate;
	}

	public ProfessionnalDomain(Professional p) {
		this.id = p.getId();
		this.code = p.getCode();
		this.name = p.getName();
		if (null != p.getCollege()) {
			this.collegeId = p.getCollege().getId();
			this.collegeName = p.getCollege().getName();
		}
		this.orgId = p.getOrgId();
		this.createdDate = createdDate;
	}
}
