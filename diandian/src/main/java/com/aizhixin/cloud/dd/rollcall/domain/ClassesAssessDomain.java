package com.aizhixin.cloud.dd.rollcall.domain;

import lombok.Data;

@Data
public class ClassesAssessDomain {
	private String name;
	private Long needAssessTotal;
	private Long AssessTotal;
	private Long notAssessTotal;
}

