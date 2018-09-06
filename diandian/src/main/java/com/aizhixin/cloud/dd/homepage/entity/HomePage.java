package com.aizhixin.cloud.dd.homepage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity(name = "DD_HOMEPAGE")
public class HomePage extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "ICON_URL")
    @Getter
    @Setter
    private String iconUrl;

    @Column(name = "IS_NEED_LOGIN")
    @Getter
    @Setter
    private Boolean isNeedLogin;

    @Column(name = "TARGET_TYPE")
    @Getter
    @Setter
    private String targetType;

    @Column(name = "TARGET_URL")
    @Getter
    @Setter
    private String targetUrl;

    @Column(name = "TITLE")
    @Getter
    @Setter
    private String title;

    @Column(name = "TYPE")
    @Getter
    @Setter
    private String type;

    @Column(name = "ROLE")
    @Getter
    @Setter
    private String role;

    @Column(name = "`ORDER`")
    @Getter
    @Setter
    private Integer order;

    @Column(name = "DOMAIN_NAME")
    @Getter
    @Setter
    private String domainName;

    @Column(name = "IS_REFRESH")
    @Getter
    @Setter
    private Boolean isRefresh;

    @Column(name = "IS_STATUSBAR")
    @Getter
    @Setter
    private Boolean isStatusBar;

    @Column(name = "target_title")
    @Getter
    @Setter
    private String targetTitle;

    @Column(name = "version")
    @Getter
    @Setter
    private String version;

    @Column(name = "ONOFF")
    @Getter
    @Setter
    private String onOff;

    @Column(name = "ORGS")
    @Getter
    @Setter
    private String orgs;

    @Column(name = "is_questionnaire")
    @Getter
    @Setter
    private Boolean isQuestionnaire;
}
