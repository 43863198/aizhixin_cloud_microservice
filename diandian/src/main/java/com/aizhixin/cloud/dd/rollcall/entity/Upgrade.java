package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

/**
 * A user.
 */
@Entity
@Table(name = "DD_UPGRADE")
public class Upgrade extends AbstractEntity {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	@Column(name = "build_number")
	@Getter
	@Setter
	private Integer buildNumber;

	@Column(name = "version")
	@Getter
	@Setter
	private String version;

	@Column(name = "version_descrip")
	@Getter
	@Setter
	private String versionDescrip;

	@Column(name = "type")
	@Getter
	@Setter
	private String type;

	@Column(name = "role")
	@Getter
	@Setter
	private String role;

	@Column(name = "download_url")
	@Getter
	@Setter
	private String downloadUrl;

	@Column(name = "is_required")
	@Getter
	@Setter
	private Boolean isRequired;

}
