package com.aizhixin.cloud.dd.orgStructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="prof")
@Data
public class Prof {
	
	@Id
	private String id;
	@Indexed
	private  Long collegeId;
	@Indexed
	private Long orgId;
	
	private Long profId;
	
	private String profName;
	
	private Long profNumber;
//	@DBRef
//	private List<Classes> classesList;
}
