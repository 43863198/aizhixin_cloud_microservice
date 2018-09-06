package com.aizhixin.cloud.rollcall.entity;

import com.aizhixin.cloud.rollcall.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

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
