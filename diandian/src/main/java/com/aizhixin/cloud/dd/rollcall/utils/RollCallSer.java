package com.aizhixin.cloud.dd.rollcall.utils;

import java.io.Serializable;

public class RollCallSer implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long courseId;
	private Long scheduleId;
	private Long scheduleRollCallId;
	private Long semesterId;
	private Long classId;

	private Long studentId;

	private Long teacherId;

	private String type;

	private Boolean canRollCall = false;

	private String lastType;

	private Boolean haveReport = false;

	private String gpsLocation;

	private String gpsDetail;

	private String gpsType;

	private String distance;

	private String signTime;

	private String createDate;

	private String lastModifiedDate;

	private String createdBy;
	private String lastModifiedBy;

	public Long getId() {

		return id;
	}

	public void setId(Long id) {

		this.id = id;
	}

	public Long getCourseId() {

		return courseId;
	}

	public void setCourseId(Long courseId) {

		this.courseId = courseId;
	}

	public Long getScheduleId() {

		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {

		this.scheduleId = scheduleId;
	}

	public Long getSemesterId() {

		return semesterId;
	}

	public void setSemesterId(Long semesterId) {

		this.semesterId = semesterId;
	}

	public Long getClassId() {

		return classId;
	}

	public void setClassId(Long classId) {

		this.classId = classId;
	}

	public Long getStudentId() {

		return studentId;
	}

	public void setStudentId(Long studentId) {

		this.studentId = studentId;
	}

	public Long getTeacherId() {

		return teacherId;
	}

	public void setTeacherId(Long teacherId) {

		this.teacherId = teacherId;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public Boolean getCanRollCall() {

		return canRollCall;
	}

	public void setCanRollCall(Boolean canRollCall) {

		this.canRollCall = canRollCall;
	}

	public String getLastType() {

		return lastType;
	}

	public void setLastType(String lastType) {

		this.lastType = lastType;
	}

	public Boolean getHaveReport() {

		return haveReport;
	}

	public void setHaveReport(Boolean haveReport) {

		this.haveReport = haveReport;
	}

	public String getGpsLocation() {

		return gpsLocation;
	}

	public void setGpsLocation(String gpsLocation) {

		this.gpsLocation = gpsLocation;
	}

	public String getGpsDetail() {

		return gpsDetail;
	}

	public void setGpsDetail(String gpsDetail) {

		this.gpsDetail = gpsDetail;
	}

	public String getGpsType() {

		return gpsType;
	}

	public void setGpsType(String gpsType) {

		this.gpsType = gpsType;
	}

	public String getDistance() {

		return distance;
	}

	public void setDistance(String distance) {

		this.distance = distance;
	}

	public String getSignTime() {

		return signTime;
	}

	public void setSignTime(String signTime) {

		this.signTime = signTime;
	}

	public String getCreateDate() {

		return createDate;
	}

	public void setCreateDate(String createDate) {

		this.createDate = createDate;
	}

	public String getLastModifiedDate() {

		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {

		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedBy() {

		return createdBy;
	}

	public void setCreatedBy(String createdBy) {

		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {

		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {

		this.lastModifiedBy = lastModifiedBy;
	}

	public Long getScheduleRollCallId() {
		return scheduleRollCallId;
	}

	public void setScheduleRollCallId(Long scheduleRollCallId) {
		this.scheduleRollCallId = scheduleRollCallId;
	}

}
