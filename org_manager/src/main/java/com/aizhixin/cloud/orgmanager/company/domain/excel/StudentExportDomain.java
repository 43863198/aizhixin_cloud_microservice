package com.aizhixin.cloud.orgmanager.company.domain.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-06
 */
@ApiModel(description = "导出的学生信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentExportDomain {
    @NotNull
    @ApiModelProperty(value = "姓名", required = true, position = 4)
    @Size(min = 0, max = 50)
    private String name;
    @ApiModelProperty(value = "姓名电话", position = 5)
    @Size(min = 0, max = 20)
    private String phone;
    @Email
    @ApiModelProperty(value = "邮箱", position = 6)
    @Size(min = 0, max = 65)
    private String email;
    @ApiModelProperty(value = "工号", position = 7)
    @Size(min = 0, max = 50)
    private String jobNumber;
    @ApiModelProperty(value = "性别(男性male|女性female)", allowableValues = "male,female", position = 8)
    @Size(min = 0, max = 10)
    private String sex;
    @ApiModelProperty(value = "班级名称", position = 10)
    private String classesName;
    @ApiModelProperty(value = "班级编码")
    private String classesCode;
    @ApiModelProperty(value = "专业名称", position = 12)
    private String professionalName;
    @ApiModelProperty(value = "学院名称", position = 14)
    private String collegeName;
    @ApiModelProperty(value = "入学日期(yyyy-MM-dd)")
    private String inSchoolDate;

}
