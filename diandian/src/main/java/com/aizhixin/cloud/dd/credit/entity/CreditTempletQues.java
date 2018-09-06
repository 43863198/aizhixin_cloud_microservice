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
@Entity(name = "dd_credit_templet_ques")
@ToString
public class CreditTempletQues extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "templet_id")
    @Getter
    @Setter
    private Long templetId;

    @Column(name = "content")
    @Getter
    @Setter
    private String content;

    @Column(name = "min_score")
    @Getter
    @Setter
    private Float minScore;

    @Column(name = "max_score")
    @Getter
    @Setter
    private Float maxScore;
}
