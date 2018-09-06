package com.aizhixin.cloud.dd.imgManager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="ImageManager")
@Data
@CompoundIndexes({@CompoundIndex(name="orgId_module_index",def="{orgId:1,module:1}")})
public class ImageManager {
	@Id
	private String id;
	/**
	 * 模块名
	 */
	private String module;
    /**
     * 图片地址
     */
    private String imgSrc;
    /**
     * 图片跳转地址
     */
    private String redirectUrl;
    
    
}
