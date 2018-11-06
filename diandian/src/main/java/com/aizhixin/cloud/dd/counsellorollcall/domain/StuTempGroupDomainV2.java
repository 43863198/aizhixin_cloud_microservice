package com.aizhixin.cloud.dd.counsellorollcall.domain;

import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcallRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@ApiModel(description = "学生辅导员点名组")
@ToString
@Data
public class StuTempGroupDomainV2 {

    private String messageId;
    private Long practiceId;

    private Long groupId;
    @ApiModelProperty(value = "组名称", dataType = "String", notes = "组名称")
    private String groupName;
    @ApiModelProperty(value = "点名次数")
    private Integer rollcallNum;
    @ApiModelProperty(value = "点名类型")
    private Integer rollcallType;

    @ApiModelProperty(value = "辅导员名称", notes = "辅导员名称")
    private String teacherName;
    @ApiModelProperty(value = "学生id", dataType = "Long", notes = "学生id")
    private Long studentId;
    @ApiModelProperty(value = "学生姓名", dataType = "String", notes = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "学号", dataType = "String", notes = "学号")
    private String psersonId;
    @ApiModelProperty(value = "头像", dataType = "String", notes = "头像")
    private String avatar;
    @ApiModelProperty(value = "班级名称", dataType = "String", notes = "班级名称")
    private String className;

    @ApiModelProperty(value = "开启状态", dataType = "Boolean", notes = "开启状态")
    private Boolean isOpen = false;
    @ApiModelProperty(value = "开启时间", dataType = "String", notes = "开启时间")
    private Timestamp openTime;

    @ApiModelProperty(value = "是否阅读", dataType = "Integer", notes = "是否阅读")
    private Boolean haveRead = false;

    @ApiModelProperty(value = "1次打卡：打卡时间  2次打卡：打卡时间段(仅显示用)")
    private String alarmTime;
    @ApiModelProperty(value = "周几")
    private String alarmModel;
    @ApiModelProperty(value = "第一次打卡时间")
    private String firstTime;
    @ApiModelProperty(value = "第一次打卡迟到时间")
    private String lateTime;
    @ApiModelProperty(value = "第二次打卡时间")
    private String secondTime;
    @ApiModelProperty(value = "第二次结束时间")
    private String endTime;

    @ApiModelProperty(value = "打卡记录")
    private List<StuRollcallReportDomainV2> reportList;

    public StuTempGroupDomainV2() {
    }

}
