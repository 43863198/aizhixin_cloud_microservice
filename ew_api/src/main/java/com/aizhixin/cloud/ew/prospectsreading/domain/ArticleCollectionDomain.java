package com.aizhixin.cloud.ew.prospectsreading.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description="文章收藏信息")
@Data
public class ArticleCollectionDomain {
	@ApiModelProperty(value = "ID", position=1)
    private Long id;
  	@ApiModelProperty(value = "标题", required = true, position=2)
  	private String title;
  	@ApiModelProperty(value = "图片", required = true, position=3)
  	private String picUrl;
  	@ApiModelProperty(value = "链接URl", required = true, position=4)
  	private String linkUrl;
  	@ApiModelProperty(value = "点击数", required = true, position=5)
  	private Long hitCount;
  	@ApiModelProperty(value = "点赞数", required = true, position=6)
  	private Long praiseCount;
  	
	public ArticleCollectionDomain(Long id, String title, String picUrl, String linkUrl,
			Long hitCount, Long praiseCount) {
		
		this.id = id;
		this.title = title;
		this.picUrl = picUrl;
		this.linkUrl = linkUrl;
		this.hitCount = hitCount;
		this.praiseCount = praiseCount;
	}

	public ArticleCollectionDomain() {
		
	}
    
    
}
