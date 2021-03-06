package com.aizhixin.cloud.dd.rollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "DD_ASSESS_FILE")
public class AssessFile extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "file_name")
    @Getter
    @Setter
    private String fileName;

    @Column(name = "file_src")
    @Getter
    @Setter
    private String fileSrc;

    @Column(name = "type")
    @Getter
    @Setter
    private String type;

    @Column(name = "file_size")
    @Getter
    @Setter
    private Long fileSize;

    @JoinColumn(name = "assess_id")
    @Getter
    @Setter
    private Long assessId;
}
