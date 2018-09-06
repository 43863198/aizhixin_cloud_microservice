package com.aizhixin.cloud.dd.orgStructure.entity;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="ClassesTeacher")
public class ClassesTeacher {
	@Id
	private String id;
	
	private Long classesId;
	
	@Indexed
	private Long userId;
}
