package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * @author hsh
 */
@Entity(name = "dd_credit")
@ToString
public class Credit extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    @Column(name = "org_id")
    @Getter
    @Setter
    private Long orgId;

    @Column(name = "teacher_id")
    @Getter
    @Setter
    private Long teacherId;

    @Column(name = "teacher_name")
    @Getter
    @Setter
    private String teacherName;

    @Column(name = "templet_id")
    @Getter
    @Setter
    private Long templetId;

    @Column(name = "templet_name")
    @Getter
    @Setter
    private String templetName;

    @Column(name = "class_count")
    @Getter
    @Setter
    private Integer classCount;

    @Column(name = "rating_stu_count")
    @Getter
    @Setter
    private Integer ratingStuCount;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "creditId")
    @Getter
    @Setter
    private List<CreditRatingPerson> ratingPersonList;
}
