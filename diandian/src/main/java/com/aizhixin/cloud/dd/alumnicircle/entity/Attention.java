package com.aizhixin.cloud.dd.alumnicircle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ATTENTION")
public class Attention extends AbstractOnlyIdAndCreatedDateEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	
	//关注人id
	@Column(name = "attention_user_id")
	@Getter
	@Setter
	private Long attentionUserId;
	
	//关注人名称
	@Column(name = "attention_name")
	@Getter
	@Setter
	private String attentionName;
	
	//被关注者id
	@Column(name = "followed_user_id")
	@Getter
	@Setter
	private Long followedUserId;
	
	//被关注者名称
	@Column(name = "followed_name")
	@Getter
	@Setter
	private String followedName;
}
