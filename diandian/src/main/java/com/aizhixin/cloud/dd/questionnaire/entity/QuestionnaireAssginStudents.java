/**
 * 
 */
package com.aizhixin.cloud.dd.questionnaire.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.dd.common.entity.AbstractEntitytwo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * 问卷分配具体学生记录
 * @author zhen.pan
 */
@Entity
@Table(name = "DD_QUESTIONNAIRE_ASSGIN_STUDENTS")
public class QuestionnaireAssginStudents extends AbstractEntitytwo implements Serializable {
	private static final long serialVersionUID = -4412554043983769571L;
	/**
	 * 问卷分配记录
	 */
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "QUESTIONNAIRE_ASSGIN_ID")
	@Getter
	@Setter
	private QuestionnaireAssgin questionnaireAssgin;
	/**
	 * 学生ID
	 */
	@Column(name = "STUDENT_ID")
	@Getter
	@Setter
	private Long studentId;
	/**
	 * 学生名称
	 */
	@Column(name = "STUDENT_NAME")
	@Getter
	@Setter
	private String studentName;
	/**
	 * 班级ID
	 */
	@Column(name = "CLASSES_ID")
	@Getter
	@Setter
	private Long classesId;
	/**
	 * 班级名称
	 */
	@Column(name = "CLASSES_NAME")
	@Getter
	@Setter
	private String classesName;
	/**
	 * 提交时间
	 */
	@Column(name = "COMMIT_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date commitDate;
	/**
	 * 评分
	 */
	@Column(name = "SCORE")
	@Getter
	@Setter
	private Integer score;
	/**
	 * 状态
	 */
	@Column(name = "STATUS")
	@Getter
	@Setter
	private Integer status = 0;
	/**
	 * 评语
	 */
	@Column(name = "comment")
	@Getter
	@Setter
	private String comment;

	public QuestionnaireAssginStudents() {
	};
	public QuestionnaireAssginStudents(Long id, QuestionnaireAssgin questionnaireAssgin, Long studentId,
			String studentName, Long classesId, String classesName, Date commitDate, Integer score, Integer status) {
		super();
		this.id = id;
		this.questionnaireAssgin = questionnaireAssgin;
		this.studentId = studentId;
		this.studentName = studentName;
		this.classesId = classesId;
		this.classesName = classesName;
		this.commitDate = commitDate;
		this.score = score;
		this.status = status;
	}
}
