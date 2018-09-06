package com.aizhixin.cloud.dd.rollcall.dto.Statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-29
 */
@Data
public class CommentDTO {
    @ApiModelProperty(value = "评论内容")
    private String content;
    @ApiModelProperty(value = "教师名称")
    private String teacherName;
    @ApiModelProperty(value = "学生名称")
    private String strudentName;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
}
