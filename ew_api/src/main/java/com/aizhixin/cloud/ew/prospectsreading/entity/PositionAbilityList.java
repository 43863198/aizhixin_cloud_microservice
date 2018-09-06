package com.aizhixin.cloud.ew.prospectsreading.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
/**
 * 职业能力分类表
 * @author bly
 * @data 2017年7月20日
 */
@Entity(name = "PROSPECT_POSITION_ABILITY_LIST")
@ToString
public class PositionAbilityList extends AbstractOnlyIdEntity {
	
	private static final long serialVersionUID = -3176563557382190284L;
	/**
     * 职位能力分类内容
     */
    @Column(name = "CONTENT")
    @Getter @Setter  private String content;
    /**
     * 职位能力分类分值
     */
    @Column(name = "SCORE")
    @Getter @Setter  private Double score;
    /**
     * 职位能力分类(10职素, 20技能, 30知识)
     */
    @Column(name = "CLASSIFICATION")
    @Getter @Setter  private Integer classification;
    /**
     * 职位解读ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    @Getter @Setter  private Position position;
    /**
     * 排序字段
     */
    @Column(name = "SORT")
    @Getter @Setter  private Integer sort;
}
