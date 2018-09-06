package com.aizhixin.cloud.dd.messege.domain;

import lombok.Data;

import java.util.List;

@Data
public class MessageResultDomain {
    private Boolean success;
    private List<MessageCidDomain> data;
}
