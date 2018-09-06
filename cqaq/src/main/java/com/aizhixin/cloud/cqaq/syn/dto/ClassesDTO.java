package com.aizhixin.cloud.cqaq.syn.dto;

import lombok.Getter;
import lombok.Setter;

public class ClassesDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String name;
    @Getter @Setter private String professionalKey;
    @Getter @Setter private String nj;


    public String keyValue() {
        return key;
    }

    public String stringValue() {
        return name + professionalKey + nj;
    }

    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }
}
