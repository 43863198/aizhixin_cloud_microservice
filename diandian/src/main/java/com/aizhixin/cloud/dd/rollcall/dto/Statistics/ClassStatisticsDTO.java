package com.aizhixin.cloud.dd.rollcall.dto.Statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-25
 */
@Data
public class ClassStatisticsDTO implements Comparable<ClassStatisticsDTO>{
    @ApiModelProperty(value = "教学班名称")
    private String className;
    @ApiModelProperty(value = "教师名称")
    private String teacherName;
    @ApiModelProperty(value = "正常数")
    private int normal;
    @ApiModelProperty(value = "请假数")
    private int leave;
    private int askForLeave;
    @ApiModelProperty(value = "到课率")
    private Double classRate;
    @ApiModelProperty(value = "课程开始时间")
    private String startTime;
    @ApiModelProperty(value = "课程开始结束")
    private String endTime;

    @Override
    public int compareTo(ClassStatisticsDTO o) {
        if (this.classRate > o.classRate) {
            return -1;//由高到底排序
        }else if (this.classRate < o.classRate){
            return 1;
        }else{
            return 0;
        }
    }

}
