
package com.aizhixin.cloud.studentpractice.task.entity;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * 任务附件对应关系表
 * @author zhengning
 *
 */
@Entity(name = "SP_TASK_FILE")
@ToString
public class TaskFile extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	/*
	 * 来源表id
	 */
	@NotNull
	@Column(name = "SOURCE_ID")
	@Getter @Setter private String sourceId;
	/*
	 * 附件id
	 */
	@Column(name = "FILE_ID")
	@Getter @Setter private String fileId;
	/*
	 * 来源(mentor-导师任务表,student-学生任务表,review-评审表)
	 */
	@Column(name = "SOURCE")
	@Getter @Setter private String source;
}
