/**
 *
 */
package com.aizhixin.cloud.io.common.exception;

import lombok.Data;

/**
 * @author zhen.pan
 */
@Data
public class ExceptionMessage {
    private Integer code;
    private String cause;

    public ExceptionMessage() {
    }

    public ExceptionMessage(Integer code, String cause) {
        this.code = code;
        this.cause = cause;
    }
}
