package com.aizhixin.cloud.ew.lostAndFound.domain;


import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(value="LFListDomain", description="失物招领列表结构")
@Data
public class LFListDomain {
	
	   //ID		
	   @ApiModelProperty(value = "ID", required = true)	
		private Long id;
	
	
	   //类型：丢失或捡到		
	   @ApiModelProperty(value = "丢失或捡到", required = true)	
		private String infoType;
	   
		//物品类型
	   @ApiModelProperty(value = "物品类型", required = true)	
		private String type;
	   
	   //性别
	   @ApiModelProperty(value = "性别", required = true)	
		private String gender;
	   
	   //图形URl
	   @ApiModelProperty(value = "图形URl", required = true)	
		private String avatar;
	   
	   //招领信息 
	   @ApiModelProperty(value = "招领信息", required = true)
		private String content;
		
	   //图片URL数组		
	 	@ApiModelProperty(value = "图片URL数组", required = true)
	 	private List<String> picUrls;	   
				
		//称呼
		@ApiModelProperty(value = "称呼", required = true)
		private String address;
		
		//联系方式
		@ApiModelProperty(value = "联系方式", required = true)
		private String contactWay;
		
		//联系号码
		@ApiModelProperty(value = "联系号码", required = true)
		private String contactNumber;
		
		//学院
		@ApiModelProperty(value = "学院", required = true)
		private String college;
		
		//表扬数
		@ApiModelProperty(value = "表扬数", required = true)
		private Integer praiseCount;
		
		//发布时间
		@ApiModelProperty(value = "发布时间", required = true)
		private String publishTime;
		
		//完成标记
		@ApiModelProperty(value = "完成标记", required = true)
		private Integer finishFlag;
		
		//点赞状态
		@ApiModelProperty(value = "点赞状态", required = true)
		private Integer status;
		
		
}
