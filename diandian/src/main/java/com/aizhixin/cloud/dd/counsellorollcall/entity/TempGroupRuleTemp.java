package com.aizhixin.cloud.dd.counsellorollcall.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by LIMH on 2017/11/29.
 * <p>
 * 分组表
 */
@Entity(name = "DD_TEMPGROUP_RULE_TEMP")
@ToString
public class TempGroupRuleTemp extends AbstractOnlyIdEntity {

    @Column(name = "group_id")
    @Getter
    @Setter
    private Long groupId;

    @Column(name = "rule_id")
    @Getter
    @Setter
    private Long ruleId;


    public TempGroupRuleTemp() {}
}
