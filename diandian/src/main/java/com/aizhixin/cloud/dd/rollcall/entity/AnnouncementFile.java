package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ANNOUNCEMENT_FILE")
public class AnnouncementFile extends AbstractOnlyIdAndCreatedDateEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "file_name")
	@Getter
	@Setter
	private String fileName;
	@Column(name = "file_src")
	@Getter
	@Setter
	private String fileSrc;
	@Column(name = "type")
	@Getter
	@Setter
	private String type;
	@Column(name = "file_size")
	@Getter
	@Setter
	private Long fileSize;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "announcement_id")
	@Getter
	@Setter
	private Announcement announcement;
}
