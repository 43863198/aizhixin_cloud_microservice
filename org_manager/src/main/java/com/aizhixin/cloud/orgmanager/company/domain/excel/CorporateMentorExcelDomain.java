package com.aizhixin.cloud.orgmanager.company.domain.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-05
 */
@Data
@NoArgsConstructor
@ToString
public class CorporateMentorExcelDomain extends LineCodeNameBaseDomain {
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "工号")
    private String jobNumber;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "企业地址")
    private String companyAddress;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "职位")
    private String position;
    @ApiModelProperty(value = "邮箱")
    private String mailbox;
    @ApiModelProperty(value = "手机")
    private  String phone;


    public CorporateMentorExcelDomain(Integer line, String name, String jobNumber, String enterpriseName, String companyAddress,
                              String department, String position, String mailbox, String phone) {
        this.line = line;
        this.name = name;
        this.jobNumber = jobNumber;
        this.enterpriseName = enterpriseName;
        this.companyAddress = companyAddress;
        this.department = department;
        this.position = position;
        this.mailbox = mailbox;
        this.phone = phone;
    }
}
