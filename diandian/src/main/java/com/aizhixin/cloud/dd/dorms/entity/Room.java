package com.aizhixin.cloud.dd.dorms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ROOM")
public class Room extends AbstractEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *  宿舍号
	 */
	@Column(name = "no")
	@Getter
	@Setter
	private String no;
	/**
	 * 楼栋id
	 */
	@Column(name = "floor_id")
	@Getter
	@Setter
	private Long floorId;
	/**
	 * 单元号
	 */
	@Column(name = "unit_no")
	@Getter
	@Setter
	private String unitNo;
	/**
	 * 楼层号
	 */
	@Column(name = "floor_no")
	@Getter
	@Setter
	private String floorNo;
	/**
	 * 房间备注
	 */
	@Column(name = "room_desc")
	@Getter
	@Setter
	private String roomDesc;
	/**
	 * 是否开放
	 */
	@Column(name = "open")
	@Getter
	@Setter
	private boolean open;
	
	/**
	 * 总床位数
	 */
	@Column(name = "beds")
	@Getter
	@Setter
	private Integer beds;
	
	/**
	 * 空床位数
	 */
	@Column(name = "em_beds")
	@Getter
	@Setter
	private Integer emBeds;
}
