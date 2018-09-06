package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_REVERT")
public class Revert extends AbstractOnlyIdAndCreatedDateEntity {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Column(name="parent_id")
	private Long parentId;
	@Getter
	@Setter
	@Column(name = "assess_id")
	private Long assessId;
	@Column(name = "from_user_id")
	@Getter
	@Setter
	private Long fromUserId;
	@Column(name = "from_user_name")
	@Getter
	@Setter
	private String fromUserName;
	@Column(name = "to_user_id")
	@Getter
	@Setter
	private Long toUserId;
	@Column(name = "to_user_name")
	@Getter
	@Setter
	private String toUserName;
	@Column(name = "content")
	@Getter
	@Setter
	private String content;
	@Column(name = "is_asses")
	@Getter
	@Setter
	private boolean asses;
}
