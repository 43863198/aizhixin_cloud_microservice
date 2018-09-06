package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import org.apache.commons.lang.StringUtils;

public class RollCallDTO extends BaseDTO {

    private Long    id                = 0L;

    private Long    scheduleId        = 0L;

    private Long    studentScheduleId = 0L;

    private Long    userId            = 0L;

    private Long    teacherId         = 0L;

    private Long    courseId          = 0L;

    private String  userName          = "";

    private String  type              = "";

    private String  className         = "";

    private Long    classId           = 0L;

    private Long    semesterId        = 0L;

    private String  distance          = "";

    private String  signTime          = "";

    private String  avatar            = "";

    private boolean isRollCall        = false;

    private boolean classroomrollcall = false;

    public boolean isClassroomrollcall() {

        return classroomrollcall;
    }

    public void setClassroomrollcall(boolean classroomrollcall) {

        this.classroomrollcall = classroomrollcall;
    }

    public String getDistance() {

        return StringUtils.isBlank(distance) ? "" : distance;
    }

    public void setDistance(String distance) {

        this.distance = distance;
    }

    public String getAvatar() {

        return avatar;
    }

    public void setAvatar(String avatar) {

        this.avatar = avatar;
    }

    public String getSignTime() {

        return StringUtils.isBlank(signTime) ? "" : signTime;
    }

    public void setSignTime(String signTime) {

        this.signTime = signTime;
    }

    public Long getSemesterId() {

        return semesterId;
    }

    public void setSemesterId(Long semesterId) {

        this.semesterId = semesterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getStudentScheduleId() {

        return studentScheduleId;
    }

    public void setStudentScheduleId(Long studentScheduleId) {

        this.studentScheduleId = studentScheduleId;
    }

    public Long getTeacherId() {

        return teacherId;
    }

    public void setTeacherId(Long teacherId) {

        this.teacherId = teacherId;
    }

    public Long getCourseId() {

        return courseId;
    }

    public void setCourseId(Long courseId) {

        this.courseId = courseId;
    }

    public boolean isRollCall() {

        return isRollCall;
    }

    public void setRollCall(boolean isRollCall) {

        this.isRollCall = isRollCall;
    }

}
