package com.aizhixin.cloud.ew.live.entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * 直播表
 * @author Rigel.ma  2017-6-5
 *
 */
@Entity(name = "live_content")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class LiveContent extends AbstractEntity {
	private static final long serialVersionUID = -5836009047318428476L;

	@Column(name = "title")
	private String title;

	@Column(name = "name")
	private String name;

	@Column(name = "coverPic")
	private String coverPic;

	@Column(name = "childPic")
	private String childPic;

	@Column(name = "data")
	private String data;

	@Column(name = "status")
	private String status;

	@Column(name = "LiveStatus")
	private String LiveStatus;

	@Column(name = "publishTime")
	private Date publishTime;

	@Column(name = "userId")
	private Long userId;

	@Column(name = "typeId")
	private Long typeId;

	@Column(name = "onlineNumber")
	private Long onlineNumber;

	@Column(name = "videoTime")
	private String videoTime;

}


