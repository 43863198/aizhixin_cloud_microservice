package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

/**
 * @author hsh
 */
@Entity(name = "dd_credit_rating_person")
@ToString
public class CreditRatingPerson extends AbstractOnlyIdAndCreatedDateEntity {
    @JoinColumn(name = "credit_id")
    @Getter
    @Setter
    private Long creditId;

    @Column(name = "stu_id")
    @Getter
    @Setter
    private Long stuId;

    @Column(name = "stu_name")
    @Getter
    @Setter
    private String stuName;

    @Column(name = "avatar")
    @Getter
    @Setter
    private String avatar;

    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId;
}
