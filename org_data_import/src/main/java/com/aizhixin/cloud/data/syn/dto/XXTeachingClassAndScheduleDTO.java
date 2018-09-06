package com.aizhixin.cloud.data.syn.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class XXTeachingClassAndScheduleDTO implements BaseDTO {
    @Getter @Setter private String key;//教学班编号
    @Getter @Setter private String courseCode;//课程编号
    @Getter @Setter private String courseName;//课程名称
    @Getter @Setter private String teacherCode;//教师编号
    @Getter @Setter private String StuCode;//学生学号
    @Getter @Setter private String StuName;//学生姓名
    @Getter @Setter private String dayOfWeek;//上课的当天是星期几
    @Getter @Setter private String classroom;//上课地点
    @Getter @Setter private String dsz;//单双周
    @Getter @Setter private String rs;//上课人数
    @Getter @Setter private String week;//起始周、结束周
    @Getter @Setter private String period;//起始节、持续节

    public String keyValue() {
        return key;
    }

    public String stringValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(courseCode).append("-").append(courseName).append(teacherCode).append("-").append(StuCode).append("-").append(StuName).append("-").append(dayOfWeek).append("-").append(classroom).append("-").append(week).append("-").append(dsz).append("-").append(period).append(rs);
        return sb.toString();
    }

    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }
}
