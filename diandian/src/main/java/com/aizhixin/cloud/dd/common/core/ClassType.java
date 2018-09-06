package com.aizhixin.cloud.dd.common.core;

public enum ClassType {
	TeachingClass(10),//教学班
	Classes(20);//行政班
	private Integer classTypeI;
	private String classTypeS;
	ClassType(Integer classTypeI){
		this.classTypeI=classTypeI;
		if(classTypeI==10){
			this.classTypeS="教学班";
		}else{
			this.classTypeI=20;
			this.classTypeS="行政班";
		}
	}
	public Integer getClassTypeI() {
		return classTypeI;
	}
	public String getClassTypeS() {
		return classTypeS;
	}
	
	
	
}
