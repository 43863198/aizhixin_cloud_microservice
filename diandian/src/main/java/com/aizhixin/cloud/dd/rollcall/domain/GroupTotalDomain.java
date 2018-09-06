package com.aizhixin.cloud.dd.rollcall.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GroupTotalDomain {
	private Long readTotal;
	private Long noReadTotal;
}
