package com.aizhixin.cloud.dd.dorms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_BED")
public class Bed extends AbstractEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 床铺名称
	 */
	@Column(name = "name")
	@Getter
	@Setter
	private String name;
	/**
	 * 床铺类型(10:上铺 20:下铺)
	 */
	@Column(name = "bed_type")
	@Getter
	@Setter
	private Integer bedType;
	/**
	 * 宿舍id
	 */
	@Column(name = "room_id")
	@Getter
	@Setter
	private Long roomId;
	/**
	 * 是否住人
	 */
	@Column(name = "live")
	@Getter
	@Setter
	private boolean live;
}
