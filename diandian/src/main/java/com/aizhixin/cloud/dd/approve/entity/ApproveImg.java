package com.aizhixin.cloud.dd.approve.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "DD_APPROVE_IMG")
@Data
public class ApproveImg extends AbstractEntity {
    private static final long serialVersionUID = -2833890921053212135L;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_approve_id")
    private CourseApprove courseApprove;
    @Column(name = "img_url")
    private String imgUrl;
}