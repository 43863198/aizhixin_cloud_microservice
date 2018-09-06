package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity(name = "DD_CONFIG")
@ToString
public class Config extends AbstractEntity {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	@Column(name = "keys")
	@Getter
	@Setter
	private String keys;

	@Column(name = "value")
	@Getter
	@Setter
	private String value;

	@Column(name = "type")
	@Getter
	@Setter
	private String type;

	@Column(name = "name")
	@Getter
	@Setter
	private String name;

	@Column(name = "remark")
	@Getter
	@Setter
	private String remark;

	@Column(name = "pid")
	@Getter
	@Setter
	private Long pid;

}
