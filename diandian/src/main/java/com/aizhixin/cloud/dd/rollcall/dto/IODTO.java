/**
 * 调用IO存储的数据传输对象
 * 
 * @author Karl Sun
 * 
 * */
package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "IO")
@Data
@EqualsAndHashCode(callSuper = false)
public class IODTO {
	// 文件名
	private String fileName;
	// 文件资源地址
	private String fileUrl;
	// 文件MD5码标示
	private String fileMD5code;

}
