package com.aizhixin.cloud.dd.homepage.domain;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class HomeDomain {
    private Long id;

    private Long createdBy;

    private Date createdDate;

    private Long lastModifiedBy;

    private Date lastModifiedDate;

    private Integer deleteFlag;

    private String iconUrl;

    private Boolean isNeedLogin;

    private String targetType;

    private String targetUrl;

    private String title;

    private String type;

    private String role;

    private Integer order;

    private String domainName;

    private Boolean isRefresh;

    private Boolean isStatusBar;

    private String targetTitle;

    private String version;

    private String onOff;

    private String orgs;

    private String logoUrl;

    private String logoUrl2;
}
