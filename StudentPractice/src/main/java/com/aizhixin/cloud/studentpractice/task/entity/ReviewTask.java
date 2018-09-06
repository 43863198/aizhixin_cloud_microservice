
package com.aizhixin.cloud.studentpractice.task.entity;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * 任务评审表
 * @author zhengning
 *
 */
@Entity(name = "SP_REVIEW_TASK")
@ToString
public class ReviewTask extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	/*
	 * 学生任务id
	 */
	@NotNull
	@Column(name = "STUDENT_TASK_ID")
	@Getter @Setter private String studentTaskId;
	/*
	 * 评审建议
	 */
	@Column(name = "REVIEW_ADVICE")
	@Getter @Setter private String reviewAdvice;
	/*
	 * 评审结果
	 */
	@Column(name = "REVIEW_RESULT")
	@Getter @Setter private String reviewResult;
	/*
	 * 评审分数
	 */
	@Column(name = "REVIEW_SCORE")
	@Getter @Setter private String reviewScore;
	/*
	 * 第几次评审
	 */
	@Column(name = "REVIEW_NUMBER")
	@Getter @Setter private int reviewNumber;
}
