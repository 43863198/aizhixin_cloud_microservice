package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "班级信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigDTO {

	private String name;
	private Long id;
	private String keys;
	private String value;
	private String type;
	private Long pid;

	public ConfigDTO(String name, Long id, String keys, String value,
			String type, Long pid) {
		super();
		this.name = name;
		this.id = id;
		this.keys = keys;
		this.value = value;
		this.type = type;
		this.pid = pid;
	}

}
