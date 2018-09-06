package com.aizhixin.cloud.dd.appeal.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "dd_appeal")
@ToString
public class Appeal extends AbstractOnlyIdAndCreatedDateEntity {

    //功能类型 10: 点名申诉
    @Column(name = "type")
    @Getter
    @Setter
    private Integer type;

    //申请人id
    @Column(name = "applicant_id")
    @Getter
    @Setter
    private Long applicantId;

    //申请人name
    @Column(name = "applicant_name")
    @Getter
    @Setter
    private String applicantName;

    //申请内容
    @Column(name = "content")
    @Getter
    @Setter
    private String content;

    //申请文件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "appealId")
    @Getter
    @Setter
    private List<AppealFile> appealFiles;

    //源数据
    @Column(name = "source_data")
    @Getter
    @Setter
    private String sourceData;

    //审批人id
    @Column(name = "inspector_id")
    @Getter
    @Setter
    private Long inspectorId;

    //审批人name
    @Column(name = "inspector_name")
    @Getter
    @Setter
    private String inspectorName;

    //10:等待审核 20:申诉成功 30:申诉失败
    @Column(name = "appeal_status")
    @Getter
    @Setter
    private Integer appealStatus;

    @Column(name = "appeal_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    protected Date appealDate;
}
