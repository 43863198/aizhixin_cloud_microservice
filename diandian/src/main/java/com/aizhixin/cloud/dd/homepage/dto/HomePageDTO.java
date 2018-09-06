package com.aizhixin.cloud.dd.homepage.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.Data;

@Data
@ApiModel(description = "教师课程列表")
public class HomePageDTO {

    private String dayOfWeek;

    private String teach_time;

    private String weekName;

    private List courseList = null;

}
