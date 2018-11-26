package com.aizhixin.cloud.sqzd.syn.dto;

import lombok.Getter;
import lombok.Setter;

public class StudentChangeDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String mc;
    @Getter @Setter private String sm;

    public String keyValue() {
        return key;
    }

    public String stringValue() {
        return mc + sm ;
    }

    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }
}
