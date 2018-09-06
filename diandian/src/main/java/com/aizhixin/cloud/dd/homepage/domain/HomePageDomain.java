package com.aizhixin.cloud.dd.homepage.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author LIMH
 * @date 2018/1/9
 */
@Data
@ToString
public class HomePageDomain {
    private Long id;
    private String title;
    private String iconUrl;
    @ApiModelProperty(value = "web/app")
    private String targetType;
    private String targetUrl;
    private String targetTitle;
    @ApiModelProperty(value = "排序")
    private Integer order;
    @ApiModelProperty(value = "banner/menu/ad/alert")
    private String type;
    @ApiModelProperty(value = "teacher/student")
    private String role;
    @ApiModelProperty(value = "on/off/many")
    private String onOff;
    private String orgs;
    private Boolean isQuestionnaire;
}
