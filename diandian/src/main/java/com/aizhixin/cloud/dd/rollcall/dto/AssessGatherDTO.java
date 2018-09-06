package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "班级信息")
@Data
public class AssessGatherDTO {
    String courseName;
    String teachTime;
    String period;
    String classRoomName;
    String averageScore;
    int assessNum;
    int fiveStar;
    int fourStar;
    int threeStar;
    int towStar;
    int oneStar;
    Long scheduleId;
    String totalScore;
}
