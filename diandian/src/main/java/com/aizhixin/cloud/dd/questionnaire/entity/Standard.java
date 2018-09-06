
package com.aizhixin.cloud.dd.questionnaire.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.aizhixin.cloud.dd.common.entity.AbstractEntitytwo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiagen
 * @ClassName: Standard
 * @Description:
 * @date 2017年5月26日 下午2:25:12
 */
@Entity
@Table(name = "DD_STANDARD")
public class Standard extends AbstractEntitytwo {
    private static final long serialVersionUID = -1685123157738744936L;
    /**
     * 序号
     */
    @Column(name = "NO")
    @Getter
    @Setter
    private Integer no;
    /**
     * 问卷
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    @Getter
    @Setter
    private Questionnaire questionnaire;
    /**
     * 等级名称
     */
    @Column(name = "LEVEL_NAME")
    @Getter
    @Setter
    private String levelName;
    /**
     * 分数段小值
     */
    @Column(name = "MIX_SCORE")
    @Getter
    @Setter
    private Integer mixScore;
    /**
     * 分数段大值
     */
    @Column(name = "MAX_SCORE")
    @Getter
    @Setter
    private Integer maxScore;

}
