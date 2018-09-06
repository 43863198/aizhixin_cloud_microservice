package com.aizhixin.cloud.paycallback.entity;


import com.aizhixin.cloud.paycallback.common.entity.AbstractUUIDEntity;
import com.aizhixin.cloud.paycallback.core.PersonalCostState;
import com.aizhixin.cloud.paycallback.domain.PersonCostExcelDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ApiModel(description="人员费用清单")
@Entity(name = "T_PERSONAL_COST")
@NoArgsConstructor
@ToString
public class PersonalCost extends AbstractUUIDEntity implements java.io.Serializable {
    @ApiModelProperty(value = "缴费科目")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_SUBJECT_ID")
    @Getter @Setter private PaymentSubject paymentSubject;
    @ApiModelProperty(value = "姓名")
    @Column(name = "NAME")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "性别")
    @Column(name = "SEX")
    @Getter @Setter private String sex;
    @ApiModelProperty(value = "身份证号码")
    @Column(name = "ID_NUMBER")
    @Getter @Setter private String idNumber;
//    @ApiModelProperty(value = "联系电话")
//    @Column(name = "PHONE")
//    @Getter @Setter private String phone;
    @ApiModelProperty(value = "录取编号")
    @Column(name = "ADMISSION_NOTICE_NUMBER")
    @Getter @Setter private String admissionNoticeNumber;

    @ApiModelProperty(value = "生源地")
    @Column(name = "STUDENT_SOURCE")
    @Getter @Setter private String studentSource;
    @ApiModelProperty(value = "学生类别(单招、统招)")
    @Column(name = "STUDENT_TYPE")
    @Getter @Setter private String studentType;
    @ApiModelProperty(value = "层次(专科、本科)")
    @Column(name = "EDU_LEVEL")
    @Getter @Setter private String eduLevel;
    @ApiModelProperty(value = "年级")
    @Column(name = "GRADE")
    @Getter @Setter private String grade;
    @ApiModelProperty(value = "校区")
    @Column(name = "SCHOOL_LOCAL")
    @Getter @Setter private String schoolLocal;

    @ApiModelProperty(value = "应缴费")
    @Column(name = "SHOULD_PAY")
    @Getter @Setter private Double shouldPay;
    @ApiModelProperty(value = "已缴费")
    @Column(name = "HAS_PAY")
    @Getter @Setter private Double hasPay;
    @ApiModelProperty(value = "缴费状态(10未缴费，20已欠费, 30已结清)")
    @Column(name = "PAYMENT_STATE")
    @Getter @Setter private Integer paymentState;
    @ApiModelProperty(value = "费用明细说明")
    @Column(name = "PAY_DESC")
    @Getter @Setter private String payDesc;
    @ApiModelProperty(value = "专业ID")
    @Column(name = "PROFESSIONAL_ID")
    @Getter @Setter private Long professionalId;
    @ApiModelProperty(value = "专业编码")
    @Column(name = "PROFESSIONAL_CODE")
    @Getter @Setter private String professionalCode;
    @ApiModelProperty(value = "专业名称")
    @Column(name = "PROFESSIONAL_NAME")
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "学院ID")
    @Column(name = "COLLEGE_ID")
    @Getter @Setter private Long collegeId;
    @ApiModelProperty(value = "学院编码")
    @Column(name = "COLLEGE_CODE")
    @Getter @Setter private String collegeCode;
    @ApiModelProperty(value = "学院名称")
    @Column(name = "COLLEGE_NAME")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "学校ID")
    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "人员状态(10正常状态，20自愿放弃)")
    @Column(name = "PERSONAL_STATE")
    @Getter @Setter private Integer personalState;
    @ApiModelProperty(value = "缴费次数统计")
    @Column(name = "PAY_NUMBER")
    @Getter @Setter private Long payNumber;

    public PersonalCost (PersonCostExcelDomain d) {
        this.name = d.getName();
        this.sex = d.getSex();
        this.idNumber = d.getIdNumber();
        this.admissionNoticeNumber = d.getAdmissionNoticeNumber();
        this.studentSource = d.getStudentSource();
        this.eduLevel = d.getEduLevel();
        this.professionalName = d.getProfessionalName();
        this.collegeName = d.getCollegeName();
        this.studentType = d.getStudentType();
        this.grade = d.getGrade();
        this.schoolLocal = d.getSchoolLocal();
        this.shouldPay = d.getShouldPay();
        this.payDesc = d.getPayDesc();
        this.personalState = PersonalCostState.NORMAL.getState();
    }
}
