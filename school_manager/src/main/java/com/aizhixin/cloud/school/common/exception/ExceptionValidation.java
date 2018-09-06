package com.aizhixin.cloud.school.common.exception;

import lombok.Data;

@Data
public class ExceptionValidation extends RuntimeException{
    private static final long serialVersionUID = -1760130004947794055L;
    protected Integer code;
    public ExceptionValidation(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
