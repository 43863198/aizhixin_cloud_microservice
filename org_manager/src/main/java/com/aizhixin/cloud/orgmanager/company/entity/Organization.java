/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 组织机构基本信息
 * @author zhen.pan
 *
 */
@Entity(name = "T_ORGANIZATION")
@ToString
public class Organization extends AbstractEntity {
	private static final long serialVersionUID = 5453428211248505505L;

	/*
	 * 机构名称
	 */
	@NotNull
	@Column(name = "NAME")
	@Getter @Setter private String name;

	/*
	 * 机构代码
	 */
	@NotNull
	@Column(name = "CODE")
	@Getter @Setter private String code;

	/*
	 * 所在省份
	 */
	@NotNull
	@Column(name = "PROVINCE")
	@Getter @Setter private String province;

	/*
	 * 域名
	 */
	@Column(name = "DOMAIN_NAME")
	@Getter @Setter private String domainName;

	/*
	 * 机构logo
	 */
	@Column(name = "LOGO")
	@Getter @Setter private String logo;

	/*
	 * 学校方形logo
	 */
	@Column(name = "SQUARE_LOGO")
	@Getter @Setter private String squareLogo;

	/*
	 * 学校长方形logo
	 */
	@Column(name = "RECTANGLE_LOGO")
	@Getter @Setter private String rectangleLogo;

	/*
	 * 是否定制化首页(启用10，不启用20)
	 */
	@Column(name = "CUSTOMER")
	@Getter @Setter private Integer customer;
}
