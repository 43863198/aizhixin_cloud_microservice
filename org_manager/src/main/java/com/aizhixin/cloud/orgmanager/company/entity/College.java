/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 学院实体对象
 *
 * @author zhen.pan
 */
@ApiModel(description = "学院完整信息")
@Entity(name = "T_COLLEGE")
@ToString
public class College extends AbstractEntity {
    private static final long serialVersionUID = 6744345245127479842L;

    @ApiModelProperty(value = "学院名称")
    @NotNull
    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

    @ApiModelProperty(value = "学院编码")
//	@NotNull
    @Column(name = "CODE")
    @Getter
    @Setter
    private String code;

    @ApiModelProperty(value = "学校ID")
    @NotNull
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long orgId;

    public College() {
    }

    public College(String name, String code, Long orgId) {
        this.name = name;
        this.code = code;
        this.orgId = orgId;
    }
}
