/**
 *
 */
package com.aizhixin.cloud.dd.questionnaire.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 问卷分配记录
 * @author zhen.pan
 */
@Entity
@Table(name = "DD_QUESTIONNAIRE_ASSGIN")
public class QuestionnaireAssgin extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = -4412554043983769571L;
	/**
	 * 问卷
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "QUESTIONNAIRE_ID")
	@Getter
	@Setter
	private Questionnaire questionnaire;
	/**
	 * 教学班ID
	 */
	@Column(name = "TEACHING_CLASS_ID")
	@Getter
	@Setter
	private Long teachingClassId;
	/**
	 * 教学班ID
	 */
	@Column(name = "TEACHING_CLASS_CODE")
	@Getter
	@Setter
	private String teachingClassCode;
	/**
	 * 教学班ID
	 */
	@Column(name = "TEACHING_CLASS_NAME")
	@Getter
	@Setter
	private String teachingClassName;
	/**
	 * 学院ID
	 */
	@Column(name = "COLLEGE_ID")
	@Getter
	@Setter
	private Long collegeId;
	/**
	 * 学院名称
	 */
	@Column(name = "COLLEGE_NAME")
	@Getter
	@Setter
	private String collegeName;
	/**
	 * 教师ID
	 */
	@Column(name = "TEACHER_ID")
	@Getter
	@Setter
	private Long teacherId;
	/**
	 * 教师名称
	 */
	@Column(name = "TEACHER_NAME")
	@Getter
	@Setter
	private String teacherName;
	/**
	 * 课程ID
	 */
	@Column(name = "COURSE_ID")
	@Getter
	@Setter
	private Long courseId;
	/**
	 * 课程名称
	 */
	@Column(name = "COURSE_NAME")
	@Getter
	@Setter
	private String courseName;
	/**
	 * 课程编码
	 */
	@Column(name = "COURSE_CODE")
	@Getter
	@Setter
	private String courseCode;
	/**
	 * 课程ID
	 */
	@Column(name = "SEMESTER_ID")
	@Getter
	@Setter
	private Long semesterId;
	/**
	 * 状态 10 分配/ 20撤销分配
	 */
	@Column(name = "STATUS")
	@Getter
	@Setter
	private String status;
	/**
	 * 分配班级类型
	 */
	@Column(name = "class_type")
	@Getter
	@Setter
	private Integer classType = 10;
	/**
	 * 行政班id
	 */
	@Column(name = "classes_id")
	@Getter
	@Setter
	private Long classesId;
	/**
	 * 行政班名称
	 */
	@Column(name = "classes_name")
	@Getter
	@Setter
	private String classesName;
	/**
	 * 行政班编号
	 */
	@Column(name = "classes_code")
	@Getter
	@Setter
	private String classesCode;
	/**
	 * 专业id
	 */
	@Column(name = "prof_id")
	@Getter
	@Setter
	private Long profId;
	/**
	 * 专业名称
	 */
	@Column(name = "prof_name")
	@Getter
	@Setter
	private String profName;

	/**
	 * 提交时间
	 */
	@Column(name = "commit_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date commitDate;
	/**
	 * 评分
	 */
	@Column(name = "score")
	@Getter
	@Setter
	private Integer score;

	/**
	 * 权重评分
	 */
	@Column(name = "weight_score")
	@Getter
	@Setter
	private Float weightScore;

	/**
	 * 状态
	 */
	@Column(name = "commit_status")
	@Getter
	@Setter
	private Integer commitStatus;
	/**
	 * 评语
	 */
	@Column(name = "comment")
	@Getter
	@Setter
	private String comment;
}
