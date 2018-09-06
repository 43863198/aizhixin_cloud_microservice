package com.aizhixin.test.task;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignDomain {
    private long scheduleId;
    private String gpsDetail;
    private String rollCallType;
    private String gpsType;
    private String gps;
    private String deviceToken;

    public SignDomain (long scheduleId, String rollCallType, String gps) {
        this.scheduleId = scheduleId;
        this.rollCallType = rollCallType;
        this.gps = gps;
    }
}
