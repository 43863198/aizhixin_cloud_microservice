/**
 * 
 */
package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 课程讲师信息
 * @author zhen.pan
 *
 */
@ApiModel(description="课程作者信息")
@Data
public class CourseAuthorDomain {
	@ApiModelProperty(value = "作者信息ID", required = false)
	protected Long id;
	@ApiModelProperty(value = "作者姓名", required = true)
	protected String name;//讲师姓名
	@ApiModelProperty(value = "作者头像图片路径", required = false)
	protected String photo;//讲师头像图片路径
	@ApiModelProperty(value = "作者头衔职位", required = false)
	protected String position;//讲师头衔
	@ApiModelProperty(value = "作者简介", required = false)
	protected String intruduce;//讲师简介
	@ApiModelProperty(value = "作者序号", required = false)
	private Integer orderNum;
	
	public CourseAuthorDomain() {}
	
	public CourseAuthorDomain(Long id, String name, String intruduce, String photo, String position,Integer orderNum) {
		this.id = id;
		this.name = name;
		this.intruduce = intruduce;
		this.photo = photo;
		this.position = position;
		this.orderNum = orderNum;
	}
}
