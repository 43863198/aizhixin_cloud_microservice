package com.aizhixin.cloud.token.service;

import com.aizhixin.cloud.token.provider.DefaultKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhen.pan on 2017/6/9.
 */
@Component
public class TokenService {
    @Autowired
    private DefaultKeyGenerator defaultKeyGenerator;

    public String generatorToken() {
        return defaultKeyGenerator.getUUID();
    }
}
