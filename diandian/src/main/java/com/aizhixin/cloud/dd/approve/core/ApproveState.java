package com.aizhixin.cloud.dd.approve.core;

public interface ApproveState {

    /**
     * 审批中
     */
    Integer APPROVE_MID=10;
    /**
     * 审批拒绝
     */
    Integer APPROVE_REFUSED=20;
    /**
     * 审批通过
     */
    Integer APPROVE_PASS=30;
    /**
     * 审批撤销
     */
    Integer APPROVE_UNDO=40;

}
