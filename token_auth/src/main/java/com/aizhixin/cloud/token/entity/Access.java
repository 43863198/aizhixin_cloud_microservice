package com.aizhixin.cloud.token.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by zhen.pan on 2017/6/6.
 */
//@RedisHash("access")
public class Access implements Serializable {
//    @Id
    @Getter @Setter private String token;
    @Getter @Setter private String appId;
    @Getter @Setter private Integer ttl;
}
