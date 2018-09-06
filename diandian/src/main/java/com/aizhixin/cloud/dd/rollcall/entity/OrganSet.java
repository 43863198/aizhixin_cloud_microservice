package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity(name = "DD_ORGAN_SET")
@ToString
public class OrganSet extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "ORGAN_ID")
    @Getter
    @Setter
    private Long organId;

    @NotNull
    @Column(name = "CALCOUNT")
    @Getter
    @Setter
    private Integer calcount;

    @Column(name = "DEVIATION")
    @Getter
    @Setter
    private Integer deviation;

    @NotNull
    @Column(name = "CONFILEVEL")
    @Getter
    @Setter
    private Integer confilevel;

    @Column(name = "ANTI_CHEATING")
    @Getter
    @Setter
    private Boolean anti_cheating;

    @Column(name = "arithmetic")
    @Getter
    @Setter
    private Integer arithmetic;

}
