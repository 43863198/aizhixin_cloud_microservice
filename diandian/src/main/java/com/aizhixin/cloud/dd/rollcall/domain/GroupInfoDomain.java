package com.aizhixin.cloud.dd.rollcall.domain;

import java.util.Date;

import lombok.Data;

@Data
public class GroupInfoDomain {
	private Long id;
	private String name;
	private Integer userType;
	private Long collegeId;
	private String collegeName;
	private Long profId;
	private String profName;
	private Long classesId;
	private String classesName;
	private String phone;
	private String sex;
	private String avatar;
	private String orgName;
	private Date haveReadDate;
}
