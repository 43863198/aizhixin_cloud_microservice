package com.aizhixin.cloud.dd.alumnicircle.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ALUMNI_CIRCLE_FILE")
public class AlumniCircleFile extends AbstractOnlyIdAndCreatedDateEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long

	 */
	private static final long serialVersionUID = 1L;
	//图片查看地址
	@Column(name = "src_url")
	@Getter
	@Setter
	private String srcUrl;
	//校友圈id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alumni_circle_id")
	@Getter
	@Setter
	private AlumniCircle alumniCircle;
	//文件大小
	@Column(name = "file_size")
	@Getter
	@Setter
	private Long fileSize;
}
