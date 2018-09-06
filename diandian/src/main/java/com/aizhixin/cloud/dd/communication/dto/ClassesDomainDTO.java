/**
 * 
 */
package com.aizhixin.cloud.dd.communication.dto;

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
@ApiModel(description="班级信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class ClassesDomainDTO extends IdUserNameBase {
	@ApiModelProperty(value = "班级编码")
	private String code;
	@ApiModelProperty(value = "入学日期(yyyy-MM-dd)")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date inSchoolDate;
	@ApiModelProperty(value = "毕业日期(yyyy-MM-dd)")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date outSchoolDate;
	@ApiModelProperty(value = "在校10、毕业20")
	private Integer schoolStatus;
	@ApiModelProperty(value = "学院ID")
	@NotNull
	@Digits(fraction = 0, integer = 18)
	private Long collegeId;

	@ApiModelProperty(value = "学院名称")
	private String collegeName;
	@ApiModelProperty(value = "专业ID", required = true)
	@NotNull
	@Digits(fraction = 0, integer = 18)
	private Long professionalId;

	@ApiModelProperty(value = "专业名称")
	private String professionalName;

	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;


	public ClassesDomainDTO(Long id, String name, Long collegeId, String collegeName, Long professionalId, String professionalName, Date createdDate) {
		super(id, name);
		this.collegeId = collegeId;
		this.collegeName = collegeName;
		this.professionalId = professionalId;
		this.professionalName = professionalName;
		this.createdDate = createdDate;
	}
}
