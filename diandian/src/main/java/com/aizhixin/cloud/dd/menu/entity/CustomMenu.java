package com.aizhixin.cloud.dd.menu.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "DD_CUSTOM_MENU")
@ToString
public class CustomMenu extends AbstractOnlyIdEntity {

    @Column(name = "user_id")
    @Getter
    @Setter
    private Long userId;

    @Column(name = "menus")
    @Getter
    @Setter
    private String menus;
}
