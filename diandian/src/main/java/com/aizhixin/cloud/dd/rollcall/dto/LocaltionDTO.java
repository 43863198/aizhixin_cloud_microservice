package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by LIMH on 2017/8/9.
 */
@Data
public class LocaltionDTO implements Serializable {
    //学生Id
    Long id;
    //学生位置
    String lo;
    private Date signTime;

    public LocaltionDTO() {
    }

    public LocaltionDTO(Long id, String lo, Date signTime) {
        this.id = id;
        this.lo = lo;
        this.signTime = signTime;
    }
}
