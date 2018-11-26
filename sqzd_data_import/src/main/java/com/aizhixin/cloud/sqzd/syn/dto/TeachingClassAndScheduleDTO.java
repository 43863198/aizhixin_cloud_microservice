package com.aizhixin.cloud.sqzd.syn.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class TeachingClassAndScheduleDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String courseCode;
    @Getter @Setter private String courseName;
    @Getter @Setter private String teacherCode;
    @Getter @Setter private String classesCode;
    @Getter @Setter private String className;
    @Getter @Setter private String dayOfWeek;
    @Getter @Setter private String classroom;
    @Getter @Setter private String rs;
    @Getter @Setter private String week;
    @Getter @Setter private String period;
    @Getter @Setter private String xn;
    @Getter @Setter private String xq;

    public String keyValue() {
        return key;
    }

    public String stringValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(courseCode).append("-").append(courseName)
        .append(teacherCode).append("-").append(classesCode)
        .append("-").append(className).append("-").append(dayOfWeek)
        .append("-").append(classroom).append("-").append(rs)
        .append("-").append(week).append("-").append(period)
        .append("-").append(xn).append("-").append(xq);
        return sb.toString();
    }

    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }
}
