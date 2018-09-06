package com.aizhixin.cloud.dd.dorms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_BED_STU")
public class BedStu extends AbstractEntity {
    /**
     * @fieldName: serialVersionUID
     * @fieldType: long

     */
    private static final long serialVersionUID = 1L;
    /**
     * 宿舍id
     */
    @Column(name = "room_id")
    @Getter
    @Setter
    private Long roomId;
    /**
     * 学生id
     */
    @Column(name = "stu_id")
    @Getter
    @Setter
    private Long stuId;

    /**
     * 学生姓名
     */
    @Column(name = "stu_name")
    @Getter
    @Setter
    private String stuName;

    /**
     * 学生性别
     */
    @Column(name = "gender")
    @Getter
    @Setter
    private String gender;
    /**
     * 身份证号
     */
    @Column(name = "id_number")
    @Getter
    @Setter
    private String idNumber;
    /**
     * phone
     */
    @Column(name = "phone")
    @Getter
    @Setter
    private String phone;
    /**
     * 专业id
     */
    @Column(name = "prof_id")
    @Getter
    @Setter
    private Long profId;

    /**
     * 专业
     */
    @Column(name = "prof_name")
    @Getter
    @Setter
    private String profName;


    /**
     * 床铺id
     */
    @Column(name = "bed_id")
    @Getter
    @Setter
    private Long bedId;
}
