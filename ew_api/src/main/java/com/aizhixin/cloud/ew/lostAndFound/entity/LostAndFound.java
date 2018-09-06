package com.aizhixin.cloud.ew.lostAndFound.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 失物招领表
 * @author Rigel.ma  2017-04-28
 *
 */
@Entity(name = "LF_LOST_FOUND")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class LostAndFound extends AbstractEntity{
private static final long serialVersionUID = -5836009047318428476L;
	
	/**
	 * 信息类型：丢失或捡到
	 */	
	@Column(name = "INFO_TYPE")
	private Integer infoType;
	
	/**
	 * 物品类型
	 */	
	@Column(name = "TYPE_ID")
	private Long typeId;
	/**
	 * 招领内容 
	 */
	@Column(name = "CONTENT")
	private String content;
	
	/**
	 * 图片一URL
	 */
	@Column(name = "PICURL1")
	private String picUrl1;
	

	/**
	 * 图片二URL
	 */
	@Column(name = "PICURL2")
	private String picUrl2;
	
	/**
	 * 图片三URL
	 */
	@Column(name = "PICURL3")
	private String picUrl3;
	
	/**
	 * 组织机构
	 */
	@Column(name = "ORGAN_ID")
	private Long organId;
	
	/**
	 * 创建人姓名
	 */
	@Column(name = "CREATED_NAME")
	private String userName;
	
	/**
	 * 称呼
	 */
	@Column(name = "ADDRESS")
	private String address;
	
	/**
	 * 联系方式
	 */
	@Column(name = "CONTACT_WAY")
	private String contactWay;
	
	/**
	 * 联系号码
	 */
	@Column(name = "CONTACT_NUMBER")
	private String contactNumber;
	
	/**
	 * 完成标志
	 */
	@Column(name = "FINISH_FLAG")
	private Integer finishFlag;
		
	
	/**
	 * 表扬或心痛数
	 */
	@Column(name = "PRAISECOUNT")
	private Integer praiseCount;
	
	/**
	 * 学院
	 */
	@Column(name = "COLLEGE")
	private String college;
	
	/**
	 * 学校
	 */
	@Column(name = "ORGAN")
	private String organ;
	/**
	 *备注
	 */
	@Column(name = "MEMO")
	private String memo;
	
}
