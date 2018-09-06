package com.aizhixin.cloud.dd.dorms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ROOM_ASSGIN")
public class RoomAssgin extends AbstractEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 学校id
	 */
	@Column(name = "org_id")
	@Getter
	@Setter
	private Long orgId;
	/**
	 * 专业id
	 */
	@Column(name = "prof_id")
	@Getter
	@Setter
	private Long profId;
	
	/**
	 * 专业名称
	 */
	@Column(name = "prof_name")
	@Getter
	@Setter
	private String profName;
	/**
	 * 学院id
	 */
	@Column(name = "college_id")
	@Getter
	@Setter
	private Long collegeId;
	
	/**
	 * 学院名称
	 */
	@Column(name = "college_name")
	@Getter
	@Setter
	private String collegeName;
	/**
	 * 宿舍id
	 */
	@Column(name = "room_id")
	@Getter
	@Setter
	private Long roomId;
	/**
	 * 性别类型
	 */
	@Column(name = "sex_type")
	@Getter
	@Setter
	private Integer sexType;

	/**
	 * 辅导员id
	 */
	@Column(name = "counselor_ids")
	@Getter
	@Setter
	private String counselorIds;

	/**
	 * 辅导员名称
	 */
	@Column(name = "counselor_names")
	@Getter
	@Setter
	private String counselorNames;
}
