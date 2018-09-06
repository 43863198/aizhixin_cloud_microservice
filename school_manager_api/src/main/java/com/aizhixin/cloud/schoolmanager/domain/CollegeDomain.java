/**
 * 
 */
package com.aizhixin.cloud.schoolmanager.domain;

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
@ApiModel(description="学院信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class CollegeDomain extends IdUserNameBase {
	@ApiModelProperty(value = "组织机构ID", required = true)
	@NotNull
	@Digits(fraction = 0, integer = 18)
	private Long orgId;

	@ApiModelProperty(value = "学院编码")
	private String code;

	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;

//	@ApiModelProperty(value = "专业名称", required = true)
//	private Set<String> profesionalNames;
	
	public CollegeDomain() {}
	
	public CollegeDomain(Long id, String code, String name, Long orgId, Date createdDate) {
		super(id, name);
		this.code = code;
		this.orgId = orgId;
		this.createdDate = createdDate;
	}
}
