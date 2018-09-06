/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.company.core.OrgCustomeOrNot;
import com.aizhixin.cloud.orgmanager.company.entity.Organization;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="组织机构信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class OrgDomain extends IdCodeNameBase  implements java.io.Serializable {
	@ApiModelProperty(value = "省名称")
	private String province;
	@ApiModelProperty(value = "是否定制化首页(启用10，不启用20)")
	private Integer customer = OrgCustomeOrNot.CLOSE.getState();
	@NotEmpty
	@ApiModelProperty(value = "域名", required = true)
	private String domainName;
	@ApiModelProperty(value = "logo URL地址")
	private String logo;
	@ApiModelProperty(value = "方形logo URL地址")
	private String ptLogo;
	@ApiModelProperty(value = "长方形logo URL地址")
	private String lptLogo;
	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	
	public OrgDomain() {}
	
	public OrgDomain(Long id, String name, String code, String domainName, String logo, String province, Date createdDate, Integer customer) {
		super(id, name, code);
		this.customer = customer;
		this.domainName = domainName;
		this.logo = logo;
		this.province = province;
		this.createdDate = createdDate;
	}

	public OrgDomain(Organization org) {
		this(org.getId(), org.getName(), org.getCode(), org.getDomainName(), org.getLogo(), org.getProvince(), org.getCreatedDate(), org.getCustomer());
	}
}
