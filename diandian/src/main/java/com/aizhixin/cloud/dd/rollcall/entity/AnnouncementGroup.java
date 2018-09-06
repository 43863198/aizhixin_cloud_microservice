package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ANNOUNCEMENT_GROUP")
public class AnnouncementGroup extends AbstractOnlyIdAndCreatedDateEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "group_id")
	@Getter
	@Setter
	private String groupId;
	@Getter
	@Setter
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "user_name")
	@Getter
	@Setter
	private String userName;
	@Column(name = "user_type")
	@Getter
	@Setter
	private Integer userType;
	@Column(name = "have_read")
	@Getter
	@Setter
	private boolean haveRead;
}
