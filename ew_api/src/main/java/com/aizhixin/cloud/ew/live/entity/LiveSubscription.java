package com.aizhixin.cloud.ew.live.entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * 直播订阅表
 * @author Rigel.ma  2017-6-5
 *
 */
@Entity(name = "live_subscription")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class LiveSubscription extends AbstractEntity {
	private static final long serialVersionUID = -5836009047318428476L;


	@Column(name = "videoId")
	private Long videoId;

	@Column(name = "userId")
	private Long userId;

	@Column(name = "typeId")
	private Long typeId;

	@Column(name = "publishTime")
	private Date publishTime;

	@Column(name = "status")
	private String status;

	@Column(name = "subscriptionTime")
	private Date subscriptionTime;


}


