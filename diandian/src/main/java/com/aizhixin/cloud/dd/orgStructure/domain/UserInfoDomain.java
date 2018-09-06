package com.aizhixin.cloud.dd.orgStructure.domain;

import lombok.Data;

@Data
public class UserInfoDomain {
	private String id;
	private Long userId;
	private String name;
	private String sex;
	private String phone;
	private String avatar;
	private Integer userType;
	private String classesName;
	private String collegeName;
	private String profName;
	private String orgName;
	private Integer teacherType;
}
