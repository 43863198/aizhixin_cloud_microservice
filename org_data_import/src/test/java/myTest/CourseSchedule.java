package myTest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@NoArgsConstructor
@ToString
public class CourseSchedule {
    @Getter @Setter private String course;
    @Getter @Setter private String teacher;
    @Getter @Setter private String classes;
    @Getter @Setter private Integer studentNums;
    @Getter @Setter private Integer dayOfWeek;
    @Getter @Setter private String week;
    @Getter @Setter private String period;
    @Getter @Setter private String classroom;

    public CourseSchedule(String classes, Integer dayOfWeek, String str) {
        this.classes = classes;
        this.dayOfWeek = dayOfWeek;
        if (!StringUtils.isEmpty(str)) {
            int p0 = 0,p1 = 0, p2 = 0;
            p0 = str.indexOf("<>");
            if (p0 > 0) {
                this.course = str.substring(0, p0);
                p2 = str.indexOf("(", p0);
                if (p2 > 0) {
                    this.week = str.substring(p0 + 2, p2);
                }

                p0 = str.indexOf(")", p2);
                if (p0 > 0) {
                    this.period = str.substring(p2 + 1, p0);
                    if (null != this.period) {
                        this.period = this.period.replace(',', '-');
                    }
                }
                p1 = str.indexOf("<>", p2);
                if (p1 > 0) {
                    p2 = str.indexOf("<>", p1 + 1);
                    if (p2 > 0) {
                        this.classroom = str.substring(p1 + 2, p2);
                        p1 = str.indexOf("[", p2);
                        if (p1 > 0) {
                            this.teacher = str.substring(p2 + 2, p1);
                            p2 = str.indexOf("人", p1);
                            if (p2 > 0) {
                                this.studentNums = Integer.valueOf(str.substring(p1 + 1, p2));
                            }
                        }
                    }
                }
            }
        }
    }
}
