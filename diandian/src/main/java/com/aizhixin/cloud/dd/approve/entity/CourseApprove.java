package com.aizhixin.cloud.dd.approve.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "DD_COURSE_APPROVE")
@Data
public class CourseApprove extends AbstractEntity {

     private static final long serialVersionUID = -2833890921053212135L;
    /**
     * 审批类型
     */
    @Column(name = "approve_type")
     private String approveType;
    /**
     * 审批编号
     */
    @Column(name = "approve_num")
    private String approveNum;
    /**
     * 审批详情
     */
     @Column(name = "context")
     private String context;
    /**
     * 审批人
     */
    @Column(name = "approve_user_id")
     private Long approveUserId;
    /**
     * 审批状态
     */
    @Column(name = "approve_state")
    private  Integer approveState=10;
    /**
     * 审批意见
     */
    @Column(name = "approve_opinion")
    private String approveOpinion;
    /**
     * 审批时间
     */
    @Column(name = "approve_date")
    private Date approveDate;
    /**
     * 审批结果图片集
     */
    @Column(name = "approve_img_list")
    private String approveImgResultList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "courseApprove")
    private List<ApproveImg> approveImgList;
}
