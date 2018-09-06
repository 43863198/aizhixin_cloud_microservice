package com.aizhixin.cloud.studentpractice.common.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "消息统计")
@Data
@EqualsAndHashCode(callSuper = false)
public class PushMessageStatusDTO {

	private String module;

	private String function;

	private String lastPushTime;

	private Integer pushCount;

	private Integer notRead;

}
