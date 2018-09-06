
package com.aizhixin.cloud.studentpractice.task.entity;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * 任务附件表
 * @author zhengning
 *
 */
@Entity(name = "SP_FILE")
@ToString
public class File extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	
	/*
	 * 附件名称
	 */
	@Column(name = "FILE_NAME")
	@Getter @Setter private String fileName;
	/*
	 * 附件保存地址
	 */
	@Column(name = "SRC_URL")
	@Getter @Setter private String srcUrl;
	
	@NotNull
	@Column(name = "SOURCE_ID")
	@Getter @Setter private String sourceId;
}
