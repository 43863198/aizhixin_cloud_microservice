package com.aizhixin.cloud.sqzd.syn.dto;

public interface BaseDTO {
    String keyValue();

    String stringValue();

    boolean eq(BaseDTO dto);
}
