package com.aizhixin.cloud.dd.rollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity(name = "dd_rollcall_appeal_file")
@ToString
public class RollCallAppealFile extends AbstractOnlyIdAndCreatedDateEntity {
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

    @JoinColumn(name = "appeal_id")
    @Getter
    @Setter
    private Long appealId;
}
