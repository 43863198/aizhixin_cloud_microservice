package com.aizhixin.cloud.orgmanager.common.domain;

import lombok.Data;

@Data
public class DataSynDomain<T> {
    private String type ;
    private T data;
}
