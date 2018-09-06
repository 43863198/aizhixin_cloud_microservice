package com.aizhixin.cloud.dd.orgStructure.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="TeachingClassTeacher")
public class TeachingClassTeacher {
	@Id
	private String id;
	
	@Indexed
	private Long teachingClassId;
	
	@Indexed
	private Long teacherId;
}
