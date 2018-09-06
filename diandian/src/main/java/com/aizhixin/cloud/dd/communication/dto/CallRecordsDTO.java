package com.aizhixin.cloud.dd.communication.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "通话记录")
@Data
public class CallRecordsDTO {
    @ApiModelProperty(value = "学生id", required = false)
    Long   studentId;
    @ApiModelProperty(value = "学生手机号码", required = false)
    String dailingPhone;
    @ApiModelProperty(value = "被拨打学生id", required = false)
    Long   calledStudentId;
    @ApiModelProperty(value = "被拨打学生号码", required = false)
    String calledPhone;

}
