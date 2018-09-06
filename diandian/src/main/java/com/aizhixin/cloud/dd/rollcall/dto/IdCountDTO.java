package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class IdCountDTO {
    Long id;
    Long count;

    public IdCountDTO(Long id, Long count) {
        this.id = id;
        this.count = count;
    }
}
