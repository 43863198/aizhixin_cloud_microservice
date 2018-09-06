package com.aizhixin.cloud.dd.communication.dto;

import java.io.Serializable;

public class RollCallReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long studentId;

    private String studentName;
    private Long classId;
    private String className;
    private Long rollCallEverId;
    private Integer lookStatus;
    
    /**
     * 经纬度信息
     */

    private String gpsLocation;

    /**
     * gps详细位置信息
     */

    private String gpsDetail;

    private String signTime;

    private boolean haveReport = false;

    private boolean leaveStatus = false;


    public boolean isLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(boolean leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public Long getClassId() {

        return classId;
    }

    public void setClassId(Long classId) {

        this.classId = classId;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Long getStudentId() {

        return studentId;
    }

    public void setStudentId(Long studentId) {

        this.studentId = studentId;
    }

    public String getStudentName() {

        return studentName;
    }

    public void setStudentName(String studentName) {

        this.studentName = studentName;
    }

    public Long getRollCallEverId() {

        return rollCallEverId;
    }

    public void setRollCallEverId(Long rollCallEverId) {

        this.rollCallEverId = rollCallEverId;
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

    public String getSignTime() {

        return signTime;
    }

    public void setSignTime(String signTime) {

        this.signTime = signTime;
    }

    public boolean isHaveReport() {

        return haveReport;
    }

    public void setHaveReport(boolean haveReport) {

        this.haveReport = haveReport;
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {

        this.className = className;
    }

    public Integer getLookStatus() {
		return lookStatus;
	}

	public void setLookStatus(Integer lookStatus) {
		this.lookStatus = lookStatus;
	}
}
