/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="课程信息")
@Data
public class CourseDomain extends IdUserNameBase {

	@ApiModelProperty(value = "课程编码")
	private String code;

	@ApiModelProperty(value = "学分")
	private Float credit;

	@ApiModelProperty(value = "课程描述", position=4)
	@Getter
	@Setter
	protected String courseDesc;
	@ApiModelProperty(value = "课程附加属性，扩展字段", position=5)
	@Getter
	@Setter
	protected String courseProp;
	@ApiModelProperty(value = "学校ID", required = true, position=8)
	@NotNull
	@Digits(fraction = 0, integer = 18)
	private Long orgId;

	@ApiModelProperty(value = "创建日期", position=12)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
//	@ApiModelProperty(value = "用户ID", required = true, position=30)
//	@Getter @Setter protected Long userId;
	@ApiModelProperty(value = "创建课程来源，10：学校，20：自建")
	private Integer source=10;
	public CourseDomain() {}

	public CourseDomain(Long id, String name, String code,String courseDesc, Date createdDate, String courseProp, Float credit) {
		this (id, name, code, courseDesc, createdDate);
		this.courseProp = courseProp;
		this.credit = credit;
	}
	
	public CourseDomain(Long id, String name, String code,String courseDesc, Date createdDate) {
		super(id, name);
		this.code = code;
		this.courseDesc = courseDesc;
		this.createdDate = createdDate;
	}

	public CourseDomain(Long id, String code, String name, Long orgId) {
		super(id, name);
		this.code = code;
		this.orgId = orgId;
	}
}
