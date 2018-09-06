package com.aizhixin.cloud.dd.monitor.dto;

import lombok.Data;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Data
public class DaybreakDTO {
    Long orgId;
    String orgName;
    Integer scheduleSize;
    Integer successSize;
    Integer failSize;

}
