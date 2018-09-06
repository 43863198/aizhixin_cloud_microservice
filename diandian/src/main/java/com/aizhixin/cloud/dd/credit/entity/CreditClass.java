package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author hsh
 */
@Entity(name = "dd_credit_class")
@ToString
public class CreditClass extends AbstractOnlyIdAndCreatedDateEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id")
    @Getter
    @Setter
    private Credit credit;

    @Column(name = "class_id")
    @Getter
    @Setter
    private Long classId;

    @Column(name = "class_name")
    @Getter
    @Setter
    private String className;

    @Column(name = "commit_count")
    @Getter
    @Setter
    private Integer commitCount;
}
