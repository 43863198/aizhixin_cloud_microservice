package com.aizhixin.cloud.dd.rollcall.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
@Entity(name="DD_ANNOUNCEMENT")
public class Announcement extends AbstractEntity{
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	
	private static final long serialVersionUID = 1L;
	@Column(name="from_user_id")
	@Getter@Setter
	private Long fromUserId;
	@Column(name="from_user_name")
	@Getter@Setter
	private String fromUserName;
	@Column(name="group_id")
	@Getter@Setter
	private String groupId;
	@Column(name="content")
	@Getter@Setter
	private String content;
	@Column(name="assess")
	@Getter@Setter
	private boolean assess;
	@Column(name="send_time")
	@Getter@Setter
	private Date sendTime;
	@Column(name="time_task")
	@Getter@Setter
	private boolean timeTask;
	@Column(name="send")
	@Getter@Setter
	private boolean send;
	@Column(name="assess_total")
	@Getter@Setter
	private Integer assessTotal;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "announcement")
    @Getter
    @Setter
	private List<AnnouncementFile>  announcementFile;
    @Column(name="send_user_total")
    @Getter
    @Setter
    private Integer  sendUserTotal;
}
