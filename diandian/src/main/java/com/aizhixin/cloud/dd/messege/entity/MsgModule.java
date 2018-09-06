package com.aizhixin.cloud.dd.messege.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name="DD_MSG_MODULE")
public class MsgModule extends AbstractEntity{

	private static final long serialVersionUID = 1L;
	/**
	 * 模块
	 */
	@Column(name="module")
	@Getter@Setter
	private String module;
	
	/**
	 * 模块
	 */
	@Column(name="function")
	@Getter@Setter
	private String function;
	/**
	 * 模块名称
	 */
	@Column(name="module_name")
	@Getter@Setter
	private String moduleName;
	/**
	 * 跳转类型
	 */
	@Column(name="jump_type")
	@Getter@Setter
	private String jumpType;
	/**
	 * 跳转地址
	 */
	@Column(name="jump_url")
	@Getter@Setter
	private String jumpUrl;
	/**
	 * 图标地址
	 */
	@Column(name="icon")
	@Getter@Setter
	private String icon;

}
