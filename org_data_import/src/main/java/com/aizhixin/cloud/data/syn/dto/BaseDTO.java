package com.aizhixin.cloud.data.syn.dto;

public interface BaseDTO {
    String keyValue();

    String stringValue();

    boolean eq(BaseDTO dto);
}
