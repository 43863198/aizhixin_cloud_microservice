package com.aizhixin.cloud.dd.imgManager.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="ImageOrg")
@Data
public class ImageOrg {
    private String id;
    @Indexed
    private Long orgId;
    private String orgName;
    @Indexed
    private String imgManagerId;
}
