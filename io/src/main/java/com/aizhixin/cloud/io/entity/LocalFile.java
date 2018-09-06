package com.aizhixin.cloud.io.entity;

import com.aizhixin.cloud.io.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhen.pan on 2017/6/13.
 */
@ApiModel(description="文件信息")
@Entity(name = "T_LOCAL_FILE")
@ToString
@NoArgsConstructor
public class LocalFile implements Serializable {
    @ApiModelProperty(value = "ID")
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID")
    @Getter @Setter private String id;

    @ApiModelProperty(value = "文件标识符")
    @Column(name = "FKEY")
    @Getter @Setter private String key;

    @ApiModelProperty(value = "文件名称")
    @Column(name = "ORIGINAL_FILENAME")
    @Getter @Setter private String originalFileName;

    @ApiModelProperty(value = "文件扩展名称")
    @Column(name = "EXT_FILE_NAME")
    @Getter @Setter private String extFileName;

    @ApiModelProperty(value = "文件相对路径")
    @Column(name = "FILE_PATH")
    @Getter @Setter private String filePath;

    @ApiModelProperty(value = "TTL")
    @Column(name = "TTL")
    @Getter @Setter private Long ttl;

    @ApiModelProperty(value = "是否删除标志")
    @Column(name = "DELETE_FLAG")
    @Getter @Setter private Integer deleteFlag = DataValidity.VALID.getState();

    @ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "CREATED_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter protected Date createdDate = new Date();
}
