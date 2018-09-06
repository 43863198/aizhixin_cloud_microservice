package com.aizhixin.cloud.dd.orgStructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="OrgInfo")
@Data
public class OrgInfo {
	@Id
	private String id;
	
	@Indexed
	private Long orgId;
	
	private String name;
	
	private String logo;
}
