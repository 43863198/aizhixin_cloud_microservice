package com.aizhixin.cloud.dd.appeal.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity(name = "dd_appeal_file")
@ToString
public class AppealFile extends AbstractOnlyIdEntity {
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
