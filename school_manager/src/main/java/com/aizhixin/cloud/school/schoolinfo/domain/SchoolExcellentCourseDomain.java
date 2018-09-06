
package com.aizhixin.cloud.school.schoolinfo.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 
 * @ClassName: SchoolExcellentCourseDomain 
 * @Description: 精品课程domain
 * @author xiagen
 * @date 2017年5月16日 下午5:20:25 
 *  
 */
@ApiModel(description="精品课程domain")
public class SchoolExcellentCourseDomain {
	 @ApiModelProperty("精品课程的id")
		@Getter@Setter
		private Long id;
		
	    @ApiModelProperty("当前用户id")
		@Getter@Setter
		private Long userId;
	    
	    @ApiModelProperty("课程id")
		@Getter@Setter
		private Long courseId;
	    
	    @ApiModelProperty("学校id")
		@Getter@Setter
		private Long orgId;
	    
	    @ApiModelProperty("精品课程介绍")
		@Getter@Setter
		private String introduction;
		
	    @ApiModelProperty("精品课程介绍图片地址")
		@Getter@Setter
		private String inUrl;
		
	    @ApiModelProperty("模版展示类型")
		@Getter@Setter
		private Integer templateShow;
	    
	    @ApiModelProperty("精品课程描述")
		@Getter@Setter
		private String courseDesc;
	    
	    @ApiModelProperty("精品课程创建时间")
		@Getter@Setter
		private Date createDate;
	    
	    @ApiModelProperty("精品课程名称")
		@Getter@Setter
		private String courseName;
	    
	    @ApiModelProperty("开卷精品课程名称")
		@Getter@Setter
		private String kfCourseName;
	    
	    @ApiModelProperty("开卷精品课程id")
	    @Getter@Setter
		private Long kfCourseId;
	    @ApiModelProperty("开卷精品课程教师")
	    @Getter@Setter
	    private List<CourseAuthorDomain> courseAuthors=new ArrayList<>();
	    
	    public  SchoolExcellentCourseDomain() {}

		public SchoolExcellentCourseDomain(Long id, Long courseId, String introduction, String inUrl,
				Integer templateShow, Date createDate,Long kfCourseId,String kfCourseName) {
			this.id = id;
			this.courseId = courseId;
			this.introduction = introduction;
			this.inUrl = inUrl;
			this.templateShow = templateShow;
			this.createDate = createDate;
			this.kfCourseId=kfCourseId;
			this.kfCourseName=kfCourseName;
		}
	    
}
