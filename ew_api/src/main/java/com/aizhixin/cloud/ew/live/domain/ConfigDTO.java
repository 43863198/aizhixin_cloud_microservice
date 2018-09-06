package com.aizhixin.cloud.ew.live.domain;

import com.aizhixin.cloud.ew.common.BaseDTO;

/**
 * @author YANGLIQIANG
 * @date 2016年02月24日
 * @time 下午3:02:39
 * 
 *       配置项的DTO
 */
public class ConfigDTO extends BaseDTO {

	private String name;
	private Long id;
	private String keys;
	private String value;
	private String type;
	private Long pid;
	
	public ConfigDTO(){
		super();
	}
	
	public ConfigDTO(String name, Long id, String keys, String value, String type, Long pid) {
		super();
		this.name = name;
		this.id = id;
		this.keys = keys;
		this.value = value;
		this.type = type;
		this.pid = pid;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((keys == null) ? 0 : keys.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}


}
