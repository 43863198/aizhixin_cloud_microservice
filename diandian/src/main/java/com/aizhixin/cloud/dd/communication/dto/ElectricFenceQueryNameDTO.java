package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * 电子围栏按学生姓名查找
 *
 */
public class ElectricFenceQueryNameDTO extends BaseDTO {
	
	@NotNull
    @ApiModelProperty(value = "id", required = false)
    protected Long            id;
	
	@NotNull
    @ApiModelProperty(value = "姓名", required = false)
    protected String            name;
	
	@NotNull
    @ApiModelProperty(value = "头像", required = false)
    protected String            avatar;
	
	@NotNull
    @ApiModelProperty(value = "经纬度", required = false)
    protected String            lltude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLltude() {
		return lltude;
	}

	public void setLltude(String lltude) {
		this.lltude = lltude;
	}

	public ElectricFenceQueryNameDTO(Long id, String name, String avatar,
			String lltude) {
		super();
		this.id = id;
		this.name = name;
		this.avatar = avatar;
		this.lltude = lltude;
	}

	public ElectricFenceQueryNameDTO() {
		super();

	}

	@Override
	public String toString() {
		return "ElectricFenceQueryNameDTO [id=" + id + ", name=" + name
				+ ", avatar=" + avatar + ", lltude=" + lltude + "]";
	}

	
	
}
