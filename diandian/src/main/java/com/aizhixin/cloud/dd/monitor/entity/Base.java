package com.aizhixin.cloud.dd.monitor.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Data
public class Base {
    protected Integer successFlag;
    protected String message;
    protected Long useTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date date;

    public Base() {}

    public Base(Integer successFlag, String message, Long useTime, Date date) {
        this.successFlag = successFlag;
        this.message = message;
        this.useTime = useTime;
        this.date = date;
    }
}
