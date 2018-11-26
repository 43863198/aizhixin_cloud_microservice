package com.aizhixin.cloud.sqzd.syn.dto;

import lombok.Getter;
import lombok.Setter;

public class CourseDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String name;

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
