package com.aizhixin.cloud.orgmanager.training.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 实训组和学生关系表
 *
 * @author wu
 */
@Entity
@Table(name = "T_GROUP_RELATION_USER")
@ToString
public class GroupRelation extends AbstractEntity {
    /**
     * 实训组Id
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    @Getter
    @Setter
    private TrainingGroup trainingGroup;
    /**
     * 用户id(学生)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @Getter
    @Setter
    private User user;

}
