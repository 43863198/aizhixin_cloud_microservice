package com.aizhixin.cloud.dd.feedback.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "反馈")
public class FeedbackRecordDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "模板id")
    private Long templetId;

    @ApiModelProperty(value = "10:教学反馈 20:督导反馈")
    private Integer type;

    @ApiModelProperty(value = "教学班id")
    private String teachingClassId;

    @ApiModelProperty(value = "选课编号")
    private String teachingClassCode;

    @ApiModelProperty(value = "教学班名称")
    private String teachingClassName;

    @ApiModelProperty(value = "授课教师")
    private String teachingClassTeacher;

    @ApiModelProperty(value = "授课教师id")
    private String teacherIds;

    @ApiModelProperty(value = "课程id")
    private String courseId;

    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @ApiModelProperty(value = "班级名称")
    private String classNames;

    @ApiModelProperty(value = "反馈者工号")
    private String jobNum;

    @ApiModelProperty(value = "反馈者名字")
    private String userName;

    @ApiModelProperty(value = "反馈者头像")
    private String userAvatar;

    @ApiModelProperty(value = "教师评价总分")
    private Integer teachingScore;

    @ApiModelProperty(value = "教师评价总分")
    private Float teachingScore2;

    @ApiModelProperty(value = "学风评价总分")
    private Integer studyStyleScore;

    @ApiModelProperty(value = "学风评价总分")
    private Float studyStyleScore2;

    @ApiModelProperty(value = "教师评价列表")
    private List<FeedbackRecordAnswerDTO> teacherQuesList;

    @ApiModelProperty(value = "学风评价列表")
    private List<FeedbackRecordAnswerDTO> styleQuesList;

    @ApiModelProperty(value = "题目列表")
    private List<FeedbackRecordAnswerDTO> quesList;

    public FeedbackRecordDTO() {

    }

    public FeedbackRecordDTO(Long id, Long templetId, String teachingClassId, String teachingClassCode, String teachingClassName, String teachingClassTeacher, String teacherIds, String courseId, String courseName, String jobNum, String userName, String userAvatar, Float teachingScore, Float studyStyleScore) {
        this.id = id;
        this.templetId = templetId;
        this.teachingClassId = teachingClassId;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.teachingClassTeacher = teachingClassTeacher;
        this.teacherIds = teacherIds;
        this.courseId = courseId;
        this.courseName = courseName;
        this.jobNum = jobNum;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.teachingScore2 = teachingScore;
        this.studyStyleScore2 = studyStyleScore;
    }
}
