package com.aizhixin.cloud.sqzd.syn.dto;

import lombok.Getter;
import lombok.Setter;

public class FdyDTO implements BaseDTO{
    @Getter@Setter
    private String key;
    @Getter@Setter
    private String fdyname;
    @Getter@Setter
    private String fdynum;
    @Override
    public String keyValue() {
        return key;
    }

    @Override
    public String stringValue() {
        return key + fdyname + fdynum;
    }

    @Override
    public boolean eq(BaseDTO dto) {
        if (this.stringValue().equals(dto.stringValue())) {
            return true;
        }
        return false;
    }

}
