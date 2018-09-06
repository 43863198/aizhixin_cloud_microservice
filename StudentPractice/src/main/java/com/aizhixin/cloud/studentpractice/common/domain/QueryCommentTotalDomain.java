package com.aizhixin.cloud.studentpractice.common.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class QueryCommentTotalDomain {
   @ApiModelProperty(value = "sourceIds 评论来源id集合",required = true)
   private  Set<String> sourceIds=new HashSet<>();
   @ApiModelProperty(value = "module 评论模块",required = true)
   private  String module;
}
