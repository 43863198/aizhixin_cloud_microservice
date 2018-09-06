package com.aizhixin.cloud.dd.communication.dto;

public class RollCallEverDTO {

    private Long    rollCallEverId;
    private String  openTime;
    private boolean status;
    private int     totalCount;
    private int     commitCount;

    public Long getRollCallEverId() {

        return rollCallEverId;
    }

    public void setRollCallEverId(Long rollCallEverId) {

        this.rollCallEverId = rollCallEverId;
    }

    public String getOpenTime() {

        return openTime;
    }

    public void setOpenTime(String openTime) {

        this.openTime = openTime;
    }

    public boolean isStatus() {

        return status;
    }

    public void setStatus(boolean status) {

        this.status = status;
    }

    public int getTotalCount() {

        return totalCount;
    }

    public void setTotalCount(int totalCount) {

        this.totalCount = totalCount;
    }

    public int getCommitCount() {

        return commitCount;
    }

    public void setCommitCount(int commitCount) {

        this.commitCount = commitCount;
    }

}
