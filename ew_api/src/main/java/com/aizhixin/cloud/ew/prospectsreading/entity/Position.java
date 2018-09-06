package com.aizhixin.cloud.ew.prospectsreading.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

/**
 * 前景职位
 */
@Entity(name = "PROSPECT_POSITION")
@ToString
public class Position extends AbstractEntity {

	private static final long serialVersionUID = 6774967014326808571L;
	/**
     * 职位名称
     */
    @Column(name = "NAME")
    @Getter @Setter  private String name;
    /**
     * 职位大类
     */
    @Column(name = "TYPE")
    @Getter @Setter  private String type;
    /**
     * 职位描述
     */
    @Column(name = "`DESC`")
    @Getter @Setter  private String desc;
    /**
     * 发布状态(0未发布，1已发布)
     */
    @Column(name = "PUBLISH_STATUS")
    @Getter @Setter  private Integer publishStatus;
}
