/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.domain;






import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="企业实践人数和企业导师人数统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseCountDomain {
	
	@ApiModelProperty(value = "ID")
	@Getter @Setter private String id;

	@ApiModelProperty(value = "企业名称")
	@Getter @Setter private String name;
	
	@ApiModelProperty(value = "所在省份")
	@Getter @Setter private String province;
	
	@ApiModelProperty(value = "所在市")
	@Getter @Setter private String city;
	
	@ApiModelProperty(value = "所在县")
	@Getter @Setter private String county;
	
	@ApiModelProperty(value = "企业地址")
	@Getter @Setter private String address;
	
	@ApiModelProperty(value = "联系方式")
	@Getter @Setter private String telephone;
	
	@ApiModelProperty(value = "邮箱")
	@Getter @Setter private String mailbox;
	
	@ApiModelProperty(value = "学生实践人次")
	@Getter @Setter private Long stuNum;
	
	@ApiModelProperty(value = "企业导师人数")
	@Getter @Setter private Long mentorNum;

}
