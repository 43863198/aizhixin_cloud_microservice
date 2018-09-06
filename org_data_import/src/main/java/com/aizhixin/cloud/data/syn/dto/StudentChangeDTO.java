package com.aizhixin.cloud.data.syn.dto;

import lombok.Getter;
import lombok.Setter;

public class StudentChangeDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String lbmc;
    @Getter @Setter private String yy;

    public String keyValue() {
        return key;
    }

    public String stringValue() {
        return lbmc + yy ;
    }

    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }
}
