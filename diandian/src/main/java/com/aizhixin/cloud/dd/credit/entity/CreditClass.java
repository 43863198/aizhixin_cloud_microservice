package com.aizhixin.cloud.dd.credit.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "last_submitted_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date lastSubmittedTime;
}
