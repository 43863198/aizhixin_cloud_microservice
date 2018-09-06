package com.aizhixin.cloud.rollcall.monitor.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Data
public class Base {
    Integer successFlag;
    String message;
    Long useTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date date;

    public Base() {}

    public Base(Integer successFlag, String message, Long useTime, Date date) {
        this.successFlag = successFlag;
        this.message = message;
        this.useTime = useTime;
        this.date = date;
    }
}
