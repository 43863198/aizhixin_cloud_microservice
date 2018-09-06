package com.aizhixin.cloud.orgmanager.classschedule.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 教学班对象
 * Created by zhen.pan on 2017/4/25.
 */
@ApiModel(description="教学班信息")
public class TeachingClassDomain extends IdNameDomain {

    @ApiModelProperty(value = "学期ID", required = true)
    @Getter @Setter private Long semesterId;
    @ApiModelProperty(value = "学期名称")
    @Getter @Setter private String semesterName;
    @ApiModelProperty(value = "学期编码")
    @Getter @Setter private String semesterCode;
    @ApiModelProperty(value = "教学班编码")
    @Getter @Setter private String code;
    @ApiModelProperty(value = "学期起始日期 ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date semesterStart;
    @ApiModelProperty(value = "学期结束日期 ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date semesterEnd;

    @ApiModelProperty(value = "课程ID", required = true)
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;

    @NotNull
    @ApiModelProperty(value = "班级关联(10)，还是学生关联(20)", required = true)
    @Getter @Setter private Integer classOrStudents;

    @ApiModelProperty(value = "教师名称(只用来显示)")
    @Getter @Setter private String teacherNames;
    @ApiModelProperty(value = "班级名称")
    private String classesNames;
    @ApiModelProperty(value = "学生数量")
    @Getter @Setter private Long studentsCount;

    @ApiModelProperty(value = "老师ID数组", required = true)
    @Size(min = 1)
    @Getter @Setter private List<Long> teacherIds;
    @ApiModelProperty(value = "班级ID数组")
    @Getter @Setter private Set<Long> classesIds;
    @ApiModelProperty(value = "学生ID数组")
    @Getter @Setter private Set<Long> studentIds;
    @ApiModelProperty(value = "操作用户ID", required = true)
    @Getter @Setter private Long userId;
    @ApiModelProperty(value = "创建来源，10:学校，20:开卷自建")
    @Setter@Getter private Integer source=10;
    @ApiModelProperty(value = "组织id")
    @Setter@Getter
    private Long orgId;
    public TeachingClassDomain() {}

    public TeachingClassDomain(Long id, String name, String code, Long courseId, String courseName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public TeachingClassDomain(Long id, String name, String code, Long semesterId, String semesterName, Long courseId, String courseName, Integer classOrStudents) {
        this(id, name, code, courseId, courseName);
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.classOrStudents = classOrStudents;
    }

    public TeachingClassDomain(Long id, String name, String code, Long semesterId, String semesterName, Long courseId, String courseName, Integer classOrStudents, String teacherNames, String classesNames, Long studentsCount) {
        this(id, name, code, semesterId, semesterName, courseId, courseName, classOrStudents);
        this.teacherNames = teacherNames;
        this.classesNames = classesNames;
        this.studentsCount = studentsCount;
    }

    public TeachingClassDomain(Long id, String name, String code, Long semesterId, String semesterName, Long courseId, String courseName, Integer classOrStudents,Long orgId) {
        this(id, name, code, semesterId,semesterName,courseId, courseName,classOrStudents);
        this.orgId = orgId;
    }
}