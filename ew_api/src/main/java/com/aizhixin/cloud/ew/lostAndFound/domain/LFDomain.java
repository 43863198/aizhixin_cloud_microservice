package com.aizhixin.cloud.ew.lostAndFound.domain;



import com.aizhixin.cloud.ew.lostAndFound.entity.Type;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(value="LFDomain", description="失物招领结构")
@Data
public class LFDomain {
	
	   //类型：丢失或捡到		
	   @ApiModelProperty(value = "丢失:0,捡到:1", required = true)	
		private Integer infoType;
	   
		//物品类型
	   @ApiModelProperty(value = "物品类型:1-8", required = true)	
		private Type type;
	   
	   //招领信息 
	   @ApiModelProperty(value = "招领信息", required = true)
		private String content;
		
		// 图片一URL		
		@ApiModelProperty(value = "图片一URL", required = true)
		private String picUrl1;
		
		 //图片二URL
		@ApiModelProperty(value = "图片二URL", required = true)
		private String picUrl2;
		
		// 图片三URL		 
		@ApiModelProperty(value = "图片三URL", required = true)
		private String picUrl3;
		
		// 组织机构
		@ApiModelProperty(value = "组织机构", required = true)
		private Long organId;
		
		// 创建人姓名
		@ApiModelProperty(value = "创建人姓名", required = true)		
		private String userName;
		
		//称呼
		@ApiModelProperty(value = "称呼", required = true)
		private String address;
		
		//联系方式
		@ApiModelProperty(value = "联系方式", required = true)
		private String contactWay;
		
		//联系号码
		@ApiModelProperty(value = "联系号码", required = true)
		private String contactNumber;
}
