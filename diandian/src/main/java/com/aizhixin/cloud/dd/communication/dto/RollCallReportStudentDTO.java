package com.aizhixin.cloud.dd.communication.dto;

import java.io.Serializable;

public class RollCallReportStudentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long teacherId;
    private String teacherName;
    private String studentName;
    private String psersonId;

    private Long rollCallEverId;

    private String gpsLocation;

    private String gpsDetail;

    private String signTime;
    private String openTime;

    private boolean haveReport = false;
    private boolean status = false;
    

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
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

    public String getOpenTime() {

        return openTime;
    }

    public void setOpenTime(String openTime) {

        this.openTime = openTime;
    }

    public boolean isHaveReport() {

        return haveReport;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPsersonId() {
        return psersonId;
    }

    public void setPsersonId(String psersonId) {
        this.psersonId = psersonId;
    }

    public void setHaveReport(boolean haveReport) {

        this.haveReport = haveReport;

    }

    public boolean isStatus() {

        return status;
    }

    public void setStatus(boolean status) {

        this.status = status;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }


}
