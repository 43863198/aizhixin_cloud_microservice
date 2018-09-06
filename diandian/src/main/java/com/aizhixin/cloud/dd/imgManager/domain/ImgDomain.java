package com.aizhixin.cloud.dd.imgManager.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImgDomain {
	@ApiModelProperty(value="主键")
	private String id;
	@ApiModelProperty(value="模块类型")
	private String module;
	@ApiModelProperty(value="学校信息")
    private List<OrgDomain> orgInfoList=new ArrayList<>();
	@ApiModelProperty(value="图片地址")
    private String imgSrc;
	@ApiModelProperty(value="跳转地址")
    private String redirectUrl;
}
