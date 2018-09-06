/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 假删除用户实体对象
 * 用于保存从用户表需要删除的数据，能用于将来恢复
 * @author zhen.pan
 *
 */
@Entity(name = "T_USER_BACK")
@ToString
@EntityListeners(AuditingEntityListener.class)
public class UserBack extends AbstractEntity {

	@ApiModelProperty(value = "用户信息")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	@Getter @Setter private User user;

	@ApiModelProperty(value = "假删除的缘由")
	@Column(name = "CAUSE")
	@Getter @Setter private String cause;

	@ApiModelProperty(value = "恢复的缘由")
	@Column(name = "RESUME_CAUSE")
	@Getter @Setter private String resumeCause;
}