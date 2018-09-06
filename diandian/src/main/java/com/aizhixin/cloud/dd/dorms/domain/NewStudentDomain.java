package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewStudentDomain {
   @ApiModelProperty(value="学生id")
   private Long stuId;
   @ApiModelProperty(value="学生名称")
   private String name;
   @ApiModelProperty(value="学生身份证")
   private String idNumber;
   @ApiModelProperty(value="专业名称")
   private String profName;

   private String collegeName;
   private String avatar;
   private String phone;
   private String studentSource;
   private String sex;
}
