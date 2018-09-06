package com.aizhixin.cloud.dd.homepage.dto;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "app首页")
@Data
@EqualsAndHashCode(callSuper = false)
public class HomePageMenuDTO implements Serializable {

    /**
     * 教学楼
     */
    private static final long serialVersionUID = 1L;

    private Long id;
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
}
