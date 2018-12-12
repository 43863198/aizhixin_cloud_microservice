package com.aizhixin.cloud.dd.rollcall.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "RollCallInitLog")
@Data
public class RollCallInitLog {
    @Id
    protected String logId;
    private Long scheduleId;
    private Long scheduleRollCallId;
    private List<RollCallLog> rollCallList;
}
