package com.aizhixin.cloud.dd.statistics.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by LIMH on 2017/8/22.
 */
@Data
@ApiModel(description = "班级信息")
public class ClassesCollegeDTO {
    Long teachingClassId;
    Long classId;
    String className;
    Long collegeId;
    String collegeName;

    public ClassesCollegeDTO(Long teachingClassId, Long classId, String className, Long collegeId, String collegeName) {
        this.teachingClassId = teachingClassId;
        this.classId = classId;
        this.className = className;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
    }

    public ClassesCollegeDTO() {
    }
}
