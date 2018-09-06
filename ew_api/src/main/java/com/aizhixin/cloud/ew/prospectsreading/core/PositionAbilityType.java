package com.aizhixin.cloud.ew.prospectsreading.core;

import lombok.Getter;

/**
 * 职位能力类型
 */
public enum PositionAbilityType {
    PROFESSIONAL_QUALITY(10),//职素
    TECHNICAL_ABILITY(20),//技能
    KNOWLEDGE(30);//知识

    @Getter private Integer state;
    @Getter private String stateDesc;
    PositionAbilityType(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "职素";
                break;
            case 20:
                this.stateDesc = "技能";
                break;
            default:
                this.state = 30;
                this.stateDesc = "知识";
        }
    }
}
