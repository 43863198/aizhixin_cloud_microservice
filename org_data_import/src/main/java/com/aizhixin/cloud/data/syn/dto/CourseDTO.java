package com.aizhixin.cloud.data.syn.dto;

import lombok.Getter;
import lombok.Setter;

public class CourseDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String name;
    @Getter @Setter private String kcxz;

    public String keyValue() {
        return key;
    }

    public String stringValue() {
        return name;
    }

    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }
}
