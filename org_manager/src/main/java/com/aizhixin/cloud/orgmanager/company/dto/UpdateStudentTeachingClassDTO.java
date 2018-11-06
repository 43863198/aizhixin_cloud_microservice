package com.aizhixin.cloud.orgmanager.company.dto;

import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import lombok.Data;

@Data
public class UpdateStudentTeachingClassDTO {
    private Long stuId;
    private Classes oldClass;
    private Classes newClass;

    public UpdateStudentTeachingClassDTO() {

    }

    public UpdateStudentTeachingClassDTO(Long stuId, Classes oldClass, Classes newClass) {
        this.stuId = stuId;
        this.oldClass = oldClass;
        this.newClass = newClass;
    }
}
