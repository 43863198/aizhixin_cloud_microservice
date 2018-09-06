
package com.aizhixin.cloud.school.schoolinfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.school.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * @ClassName: SchoolIntrduction 
 * @Description: 学校简介
 * @author xiagen
 * @date 2017年5月12日 上午11:04:08 
 *  
 */
@Entity(name="S_SCHOOLINTRODUCTION")
@ToString
public class SchoolIntrduction extends AbstractEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8298025129950848381L;
    /***
     * 学校id
     */
	@Column(name="schoolId")
	@Getter@Setter
    private Long schoolId;
	
	/**
	 * 学校简介
	 */
	@Column(name="introduction")
	@Getter@Setter
	private String introduction;

}
