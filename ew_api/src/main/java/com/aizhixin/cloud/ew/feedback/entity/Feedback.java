package com.aizhixin.cloud.ew.feedback.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 问题反馈实体类
 * 
 * @author bly
 * @data 2017年8月30日
 */
@Entity(name = "FEEDBACK")
@ToString
public class Feedback extends AbstractEntity {
	
	private static final long serialVersionUID = 1409367751865899819L;
	/**
	 * 姓名
	 */
	@Column(name = "NAME")
	@Getter @Setter private String name;
	/**
	 * 手机号
	 */
	@Column(name = "PHONE")
	@Getter @Setter private String phone;
	/**
	 * 手机设备信息
	 */
	@Column(name = "PHONE_DEVICE_INFO")
	@Getter @Setter private String phoneDeviceInfo;
	/**
	 * 学校
	 */
	@Column(name = "SCHOOL")
	@Getter @Setter private String school;
	/**
	 * 班级
	 */
	@Column(name = "CLASSES")
	@Getter @Setter private String classes;
	/**
	 * 问题描述
	 */
	@Column(name = "DESCRIPTION")
	@Getter @Setter private String description;
	/**
	 * 上传图片数组
	 */
	@Column(name = "PICTURE_URLS")
	@Getter @Setter private String pictureUrls;
}
