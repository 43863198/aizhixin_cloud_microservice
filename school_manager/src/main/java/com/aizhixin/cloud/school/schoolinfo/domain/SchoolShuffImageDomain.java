
package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 
 * @ClassName: SchoolShuffImageDomain 
 * @Description: 学校轮播图信息domain
 * @author xiagen
 * @date 2017年5月12日 上午11:27:27 
 *  
 */
@ApiModel(description = "学校轮播图管理")
public class SchoolShuffImageDomain {
    @ApiModelProperty("学校id")
    @Getter@Setter
	private Long orgId;
	
    @ApiModelProperty("学校轮播图id")
    @Getter@Setter
	private Long schoolShuffImageId;

	@ApiModelProperty("轮播图地址")
    @Getter@Setter
    private String imageUrl;
	
	@ApiModelProperty("轮播图顺序")
    @Getter@Setter
    private Integer imageSort;
	
	@ApiModelProperty("轮播图连接")
    @Getter@Setter
    private String href;
	
    public SchoolShuffImageDomain(){}
    public SchoolShuffImageDomain(Long schoolShuffImageId, String imageUrl,Integer imageSort,String href) {
		this.schoolShuffImageId = schoolShuffImageId;
		this.imageUrl = imageUrl;
		this.imageSort=imageSort;
		this.href=href;
	}

}
