package com.aizhixin.cloud.ew.article.entity;

import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论点赞表
 * 
 * @author Rigel.ma 2017-05-24
 *
 */
@Entity(name = "AM_COMMENT_PRAISE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentPraise extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318228476L;
	/**
	 * 评论ID
	 */
	@Column(name = "COMMENT_ID")
	private Long commentId;

	/**
	 * 创建人
	 */
	@Column(name = "CREATED_BY")
	private Long createdBy;

	/**
	 * 创建日期
	 */
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	/**
	 * 修改日期
	 */
	@Column(name = "LAST_MODIFIED_DATE")
	private Date modifiedDate;

	/**
	 * 点赞状态
	 */
	@Column(name = "STATUS")
	private Integer status;

}
