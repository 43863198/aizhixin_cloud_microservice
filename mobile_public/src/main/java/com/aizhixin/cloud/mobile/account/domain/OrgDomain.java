/**
 * 
 */
package com.aizhixin.cloud.mobile.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="组织机构信息")
@ToString
@EqualsAndHashCode(callSuper=false)
public class OrgDomain  implements java.io.Serializable {
	@ApiModelProperty(value = "ID")
	@Getter @Setter private Long id;

	@ApiModelProperty(value = "名称")
	@Getter @Setter private String name;

	@ApiModelProperty(value = "编号")
	@Getter @Setter private String code;

	@ApiModelProperty(value = "省名称")
	@Getter @Setter private String province;

	@ApiModelProperty(value = "是否定制化首页(启用10，不启用20)")
	@Getter @Setter private Integer customer;

	@ApiModelProperty(value = "域名")
	@Getter @Setter private String domainName;

	@ApiModelProperty(value = "logo URL地址")
	@Getter @Setter private String logo;

	@ApiModelProperty(value = "方形logo URL地址")
	@Getter @Setter private String ptLogo;

	@ApiModelProperty(value = "长方形logo URL地址")
	@Getter @Setter private String lptLogo;

	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Getter @Setter private Date createdDate;
}
