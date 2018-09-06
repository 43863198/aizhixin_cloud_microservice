package com.aizhixin.cloud.orgmanager.training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import java.util.Date;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
public class TrainingGroupListInfoDTO {
    @ApiModelProperty(value = "实训小组id")
    private Long id;
    @ApiModelProperty(value = "实训小组名称")
    private String groupName;
    @ApiModelProperty(value = "实训小组开始时间")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDate;
    @ApiModelProperty(value = "结束时间")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endDate;
    @ApiModelProperty(value = "企业导师id")
    private Long corporateMentorsId;
    @ApiModelProperty(value = "企业导师姓名")
    private String corporateMentorsName;
    @ApiModelProperty(value = "学校老师id")
    private Long teacherId;
    @ApiModelProperty(value = "学校老师姓名")
    private String teacherName;
    @ApiModelProperty(value = "学生人数")
    private Long studentCount;
    @ApiModelProperty(value = "学校老师姓名")
    private String status;
}
