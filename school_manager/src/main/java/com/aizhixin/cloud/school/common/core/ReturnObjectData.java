package com.aizhixin.cloud.school.common.core;

import lombok.Data;

@Data
public class ReturnObjectData<T>{
    private Boolean result;
    private T data;
    private String code;
}
