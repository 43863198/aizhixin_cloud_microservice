package com.aizhixin.cloud.dd.orgStructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="College")
@Data
public class College {
	@Id
	protected String id;
	@Indexed
	private Long orgId;
	
	private Long collegeId;
	
	private String collegeName;
	
	private Long pepleNumber;
	
//	@DBRef
//	private List<prof> profs;
}
