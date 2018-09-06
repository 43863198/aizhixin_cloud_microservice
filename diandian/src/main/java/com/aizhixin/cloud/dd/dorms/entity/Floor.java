package com.aizhixin.cloud.dd.dorms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_FLOOR")
public class Floor extends AbstractEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 学校id
	 */
	@Column(name="org_id")
	@Getter@Setter
	private Long orgId;
	
	/**
	 * 楼栋名称
	 */
	@Column(name = "name")
	@Getter
	@Setter
	private String name;
	
	/**
	 * 楼栋类型
	 */
	@Column(name = "floor_type")
	@Getter
	@Setter
	private Integer floorType;
	
	/**
	 * 楼层总数
	 */
	@Column(name = "floor_num")
	@Getter
	@Setter
	private Integer floorNum;
	/***
	 * 单元数
	 */
	@Column(name = "unit_num")
	@Getter
	@Setter
	private Integer unitNum;
	/**
	 * 楼栋备注
	 */
	@Column(name = "floor_desc")
	@Getter
	@Setter
	private String floorDesc;
	/**
	 * 类型图片地址
	 */
	@Column(name = "floor_image")
	@Getter
	@Setter
	private String floorImage;
}
