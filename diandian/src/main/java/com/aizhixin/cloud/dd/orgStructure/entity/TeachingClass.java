package com.aizhixin.cloud.dd.orgStructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Document(collection="TeachingClass")
@Data
public class TeachingClass {
	@Id
	protected String id;
	//教学班id
	@Indexed
	private Long teachingClassId;
	//教学班名称
	private String teachingClassName;
	//教学班人数
	private Long pepleNumber;
	@Indexed
	private Long orgId;
	//学期id
	private Long semesterId;

	private String classNames;
}
