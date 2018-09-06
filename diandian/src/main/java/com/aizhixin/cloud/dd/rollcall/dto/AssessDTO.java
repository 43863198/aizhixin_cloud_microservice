package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel(description = "评论")
@Data
public class AssessDTO {
	private Long id;
	private Long studentId;
	private Long teacherId;
	private Long scheduleId;
	private Long semesterId;
	private String content;
	private Integer score;
	private Long classesId;
	private List<AssessFileDTO> files;
}
