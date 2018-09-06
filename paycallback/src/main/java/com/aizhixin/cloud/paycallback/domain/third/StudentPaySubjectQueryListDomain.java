package com.aizhixin.cloud.paycallback.domain.third;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ApiModel(description="学生缴费项查询结果对象")
@NoArgsConstructor
@ToString
public class StudentPaySubjectQueryListDomain {
    @ApiModelProperty(value = "缴费者id（学号/工号等）我们这儿是身份证号码")
    @Getter @Setter private String user_no;

    @ApiModelProperty(value = "当前页码")
    @Getter @Setter private String page_no;

    @ApiModelProperty(value = "一页显示数量，默认为15条")
    @Getter @Setter private String page_size;

    @ApiModelProperty(value = "总页数")
    @Getter @Setter private String total_page;

    @ApiModelProperty(value = "缴费项列表")
    @Getter @Setter private List<StudentPaySubjectDomain> data;

    public StudentPaySubjectQueryListDomain(String user_no, String page_no, String page_size) {
        this.user_no = user_no;
        this.page_no = page_no;
        this.page_size = page_size;
    }
}
