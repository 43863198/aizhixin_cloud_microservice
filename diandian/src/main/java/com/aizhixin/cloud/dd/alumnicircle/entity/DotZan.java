package com.aizhixin.cloud.dd.alumnicircle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_DOT_ZAN")
public class DotZan extends AbstractOnlyIdAndCreatedDateEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	//点赞人id
	@Column(name = "user_id")
	@Getter
	@Setter
	private Long userId;
	
	//点赞人名称
	@Column(name = "user_name")
	@Getter
	@Setter
	private String userName;
	
	//校友圈id
	@Column(name = "alumni_circle_id")
	@Getter
	@Setter
	private Long alumniCircleId;
}
