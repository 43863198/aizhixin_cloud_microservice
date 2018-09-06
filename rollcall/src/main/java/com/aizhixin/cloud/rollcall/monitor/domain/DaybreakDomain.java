package com.aizhixin.cloud.rollcall.monitor.domain;

import lombok.Data;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Data
public class DaybreakDomain {
    Long orgId;
    String orgName;
    Integer scheduleSize;
    Integer successSize;
    Integer failSize;
    String teachDate;
    Long useTime;

    public DaybreakDomain(Long orgId, String orgName, Integer scheduleSize, Integer successSize, Integer failSize, String teachDate, Long useTime) {
        this.orgId = orgId;
        this.orgName = orgName;
        this.scheduleSize = scheduleSize;
        this.successSize = successSize;
        this.failSize = failSize;
        this.teachDate = teachDate;
        this.useTime = useTime;
    }
}
