package com.aizhixin.cloud.cqaq.syn.dto;

public interface BaseDTO {
    String keyValue();

    String stringValue();

    boolean eq(BaseDTO dto);
}
