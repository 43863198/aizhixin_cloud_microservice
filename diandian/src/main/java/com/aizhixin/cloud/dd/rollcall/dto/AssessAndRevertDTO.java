package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aizhixin.cloud.dd.rollcall.entity.AssessFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="评论及回复")
public class AssessAndRevertDTO {
	@ApiModelProperty(value="评论表id")
	private  Long id;
	@ApiModelProperty(value="评论者id")
	private  Long commentId;
	@ApiModelProperty(value="评论者名称")
	private  String commentName;
	@ApiModelProperty(value="评论者头像")
	private String avatar;
	@ApiModelProperty(value="评论者内容")
	private String content;
	@ApiModelProperty(value="评论时间")
	private Date createDate;
	@ApiModelProperty(value="评论分数")
	private Integer score;
	@ApiModelProperty(value="评论回复数")
	private Integer revertTotal;
	@ApiModelProperty(value="评论下的回复内容")
	private List<RevertDTO> rdl=new ArrayList<>();
	@ApiModelProperty(value="是否匿名展示")
	private  boolean anonymity;
	@ApiModelProperty(value="行政班id")
	private Long classesId;
	@ApiModelProperty(value="行政班名称")
	private String className;

	private List<AssessFile> files;

	public  AssessAndRevertDTO(){}
	public AssessAndRevertDTO(Long id, Long commentId, String commentName, String content, Date createDate,Integer revertTotal,boolean anonymity,Integer score) {
		super();
		this.id = id;
		this.commentId = commentId;
		this.commentName = commentName;
		this.content = content;
		this.createDate = createDate;
		this.revertTotal=revertTotal;
		this.anonymity=anonymity;
		this.score=score;
	}
	
}
