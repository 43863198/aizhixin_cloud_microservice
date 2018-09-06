package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class StudentInfoDTOV2 {
	//序号
	private Integer no;
	//学生id
	private Long stuId;
	//学生名称
	private String stuName;
	//行政班名称
	private String classesName;
	//学院名称
	private String collegeName;
	//专业名称
	private String profName;
	//辅导员名称
	private String teacherName;
}
