/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zhen.pan
 */
@ApiModel(description = "班级信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class ClassesDomain extends IdUserNameBase implements java.io.Serializable {
    @ApiModelProperty(value = "班级编码")
    private String code;
    @ApiModelProperty(value = "入学日期(yyyy-MM-dd)")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inSchoolDate;
    @ApiModelProperty(value = "毕业日期(yyyy-MM-dd)")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date outSchoolDate;
    @ApiModelProperty(value = "在校10、毕业20")
    private Integer schoolStatus;
    @ApiModelProperty(value = "学年(2016)")
    private String teachingYear;
    @ApiModelProperty(value = "学院ID")
    @NotNull
    @Digits(fraction = 0, integer = 18)
    private Long collegeId;

    @ApiModelProperty(value = "学院名称")
    private String collegeName;
    @ApiModelProperty(value = "专业ID", required = true)
    @NotNull
    @Digits(fraction = 0, integer = 18)
    private Long professionalId;

    @ApiModelProperty(value = "专业名称")
    private String professionalName;
    @ApiModelProperty(value = "专业编码")
    private String professionalCode;

    @ApiModelProperty(value = "导员")
    private String teachers;

    @ApiModelProperty(value = "学制")
    private String schoolingLength;

    @ApiModelProperty(value = "创建日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    public ClassesDomain() {
    }

    public ClassesDomain(Long id, String name, String code, String teachingYear, Long collegeId, String collegeName, Long professionalId, String professionalName, Date createdDate, Integer schoolStatus, Date inSchoolDate, Date outSchoolDate) {
        super(id, name);
        this.code = code;
        this.teachingYear = teachingYear;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.createdDate = createdDate;
        this.schoolStatus = schoolStatus;
        this.inSchoolDate = inSchoolDate;
        this.outSchoolDate = outSchoolDate;
    }

    public ClassesDomain(Long id, String name, String code, String teachingYear, Long collegeId, String collegeName, Long professionalId, String professionalName, Date createdDate, Integer schoolStatus, Date inSchoolDate, Date outSchoolDate, String schoolingLength) {
        super(id, name);
        this.code = code;
        this.teachingYear = teachingYear;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.createdDate = createdDate;
        this.schoolStatus = schoolStatus;
        this.inSchoolDate = inSchoolDate;
        this.outSchoolDate = outSchoolDate;
        this.schoolingLength = schoolingLength;
    }

    public ClassesDomain(Classes c) {
        this.id = c.getId();
        this.name = c.getName();
        this.code = c.getCode();
        this.teachingYear = c.getTeachingYear();
        this.createdDate = c.getCreatedDate();
        this.schoolStatus = c.getSchoolStatus();
        this.inSchoolDate = c.getInSchoolDate();
        this.outSchoolDate = c.getOutSchoolDate();
        if (null != c.getProfessional()) {
            this.professionalId = c.getProfessional().getId();
            this.professionalName = c.getProfessional().getName();
        }
        if (null != c.getCollege()) {
            this.collegeId = c.getCollege().getId();
            this.collegeName = c.getCollege().getName();
        }
        this.schoolingLength = c.getSchoolingLength();
    }
}
