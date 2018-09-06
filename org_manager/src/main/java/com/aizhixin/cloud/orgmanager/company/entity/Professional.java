/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 专业实体对象
 * @author zhen.pan
 *
 */
@ApiModel(description="专业完整信息")
@Entity(name = "T_PROFESSIONAL")
@ToString
public class Professional extends AbstractEntity {
	private static final long serialVersionUID = 4491928086409136372L;

	@ApiModelProperty(value = "专业名称")
	@NotNull
	@Column(name = "NAME")
	@Getter @Setter private String name;

	@ApiModelProperty(value = "专业编码")
//	@NotNull
	@Column(name = "CODE")
	@Getter @Setter private String code;

	@JsonIgnore
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COLLEGE_ID")
	@Getter @Setter private College college;

	@ApiModelProperty(value = "学校ID")
	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
}
