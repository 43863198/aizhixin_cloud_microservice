
package com.aizhixin.cloud.studentpractice.task.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 实践签到详情表
 * @author zhengning
 *
 */
@Entity(name = "SP_SIGN_DETAIL")
@ToString
public class SignDetail  {
	
	@ApiModelProperty(value = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter @Setter private Long id;

	@ApiModelProperty(value = "学生id")
	@Column(name = "STUDENT_ID")
	@Getter @Setter private Long studentId;
	
	@ApiModelProperty(value = "签到地图经纬度")
	@Column(name = "GPS_LOCATION")
	@Getter @Setter private String gpsLocation;
	
	@ApiModelProperty(value = "签到所在具体位置")
	@Column(name = "GPS_DETAIL")
	@Getter @Setter private String gpsDetail;
	
	@ApiModelProperty(value = "签到网络类型")
	@Column(name = "GPS_TYPE")
	@Getter @Setter private String gpsType;
	
	@ApiModelProperty(value = "签到时间")
	@Column(name = "SIGN_TIME")
	@Getter @Setter private String signTime;
	
	@ApiModelProperty(value = "签到状态[未提交:10,已到:20,未到:30,请假:40,迟到:50]")
	@Column(name = "SIGN_STATUS")
	@Getter @Setter private String signStatus;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "创建时间")
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter private Date createdDate;
	
}
