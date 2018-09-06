package com.aizhixin.cloud.orgmanager.company.domain.message;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="班级添加删除导员提示消息")
@ToString
@NoArgsConstructor
public class ClassesTeacherBO {
    @ApiModelProperty(value = "操作：add(某个班级添加某个老师为导员),delete(某个班级解除某个导员老师)")
    @Getter   @Setter  private String operator;
    @ApiModelProperty(value = "老师ID")
    @Getter   @Setter  private Long teacherId;
    @ApiModelProperty(value = "班级ID")
    @Getter  @Setter private Long classId;
}
