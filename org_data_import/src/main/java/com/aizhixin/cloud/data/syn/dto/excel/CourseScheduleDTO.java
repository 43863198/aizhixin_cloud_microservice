package com.aizhixin.cloud.data.syn.dto.excel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CourseScheduleDTO {
    @Getter @Setter private String skbj;//上课班级
    @Getter @Setter private String dayOfWeek;
    @Getter @Setter private String classroom;
//    @Getter @Setter private String rs;
    @Getter @Setter private String week;
    @Getter @Setter private String period;
    @Getter @Setter private String dsz;//单双周

    public String key() {
        StringBuilder sb = new StringBuilder();
        sb.append(classroom).append("-");
//        sb.append(rs).append("-");
        sb.append(dayOfWeek).append("-");
        sb.append(week).append("-");
        sb.append(period);
        sb.append(dsz);
        return  sb.toString();
    }
}
