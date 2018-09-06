package com.aizhixin.cloud.dd.rollcall.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class BaseDTO {

	public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
