package com.aizhixin.cloud.cqaq.syn.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class KeyNameDTO implements BaseDTO {
    @Getter @Setter private String key;
    @Getter @Setter private String name;

    public KeyNameDTO(String key, String name) {
        this.key = key;
        this.name = name;
    }

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
