package com.aizhixin.cloud.dd.alumnicircle.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_ALUMNI_CIRCLE")
public class AlumniCircle extends AbstractOnlyIdAndCreatedDateEntity {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 */
	private static final long serialVersionUID = 1L;
	// 发送者id
	@Column(name = "from_user_id")
	@Getter
	@Setter
	private Long fromUserId;
	// 发送者名称
	@Column(name = "from_user_name")
	@Getter
	@Setter
	private String fromUserName;
	// 学校id
	@Column(name = "org_id")
	@Getter
	@Setter
	private Long orgId;
	// 学校名称
	@Column(name = "org_name")
	@Getter
	@Setter
	private String orgName;
	// 学院id
	@Column(name = "college_id")
	@Getter
	@Setter
	private Long collegeId;
	// 学院名称
	@Column(name = "college_name")
	@Getter
	@Setter
	private String collegeName;
	// 发送模块
	@Column(name = "send_to_module")
	@Getter
	@Setter
	private Integer sendToModule = 0;
	// 发送内容
	@Column(name = "content")
	@Getter
	@Setter
	private String content;
	// 是否展示昵称
	@Column(name = "nick_name")
	@Getter
	@Setter
	private boolean nickName;
	//点赞统计
	@Column(name = "dz_total")
	@Getter
	@Setter
	private Integer dzTotal=0;
	//评论统计
	@Column(name = "assess_total")
	@Getter
	@Setter
	private Integer assessTotal=0;
	
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "alumniCircle")
    @Getter
    @Setter
	private List<AlumniCircleFile>  alumniCircleFile;
	//前5个评论者头像
	@Column(name = "avatars")
	@Getter
	@Setter
	private String avatars;
}
