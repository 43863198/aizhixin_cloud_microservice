package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author hsh
 */
@Entity(name = "dd_credit_templet")
@ToString
public class CreditTemplet extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "org_id")
    @Getter
    @Setter
    private Long orgId;

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    //打分题总分
    @Column(name = "total_score")
    @Getter
    @Setter
    private Float totalScore;

    //引用次数
    @Column(name = "usage_count")
    @Getter
    @Setter
    private Integer usageCount;

}
