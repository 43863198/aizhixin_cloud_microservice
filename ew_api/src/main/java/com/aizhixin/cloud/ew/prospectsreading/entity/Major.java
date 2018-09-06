package com.aizhixin.cloud.ew.prospectsreading.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

/**
 * 前景专业
 */
@Entity(name = "PROSPECT_MAJOR")
@ToString
public class Major extends AbstractEntity {

	private static final long serialVersionUID = 7208532716853003849L;
	/**
     * 专业名称
     */
    @Column(name = "NAME")
    @Getter @Setter  private String name;
    /**
     * 专业大类
     */
    @Column(name = "TYPE")
    @Getter @Setter  private String type;
    /**
     * 专业描述
     */
    @Column(name = "`DESC`")
    @Getter @Setter  private String desc;
    /**
     * 发布状态(0未发布，1已发布)
     */
    @Column(name = "PUBLISH_STATUS")
    @Getter @Setter  private Integer publishStatus;
}
