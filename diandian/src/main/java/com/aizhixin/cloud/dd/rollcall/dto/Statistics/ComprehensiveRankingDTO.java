package com.aizhixin.cloud.dd.rollcall.dto.Statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-25
 */
@Data
public class ComprehensiveRankingDTO implements Comparable<ComprehensiveRankingDTO>{
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "综合分")
    private double comprehensive;
    @ApiModelProperty(value = "班主任")
    private List<String> headmaster;
    @Override
    public int compareTo(ComprehensiveRankingDTO o) {
        if (this.comprehensive > o.comprehensive) {
            return -1;//由高到底排序
        }else if (this.comprehensive < o.comprehensive){
            return 1;
        }else{
            return 0;
        }
    }
}
