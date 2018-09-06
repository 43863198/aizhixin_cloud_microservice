package com.aizhixin.cloud.dd.rollcall.domain;

import lombok.Data;

@Data
public class ExportCourseAssesTotalDomain {
	private String courseName;
	private Long teacherTotal;
	private Double avg;
	private Double max;
	private Double mix;
	private Long etTotal;
	private Double etzb;
	private  Long netTotal;
	private Double netzb;
	private Long nToal;
	private Double nzb;
	
}
