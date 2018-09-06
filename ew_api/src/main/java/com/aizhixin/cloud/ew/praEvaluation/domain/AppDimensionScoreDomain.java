package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "AppDimensionScoreDomain", description = "维度得分结构体")
@Data
public class AppDimensionScoreDomain implements Comparable<Object> {

	// 维度ID
	@ApiModelProperty(value = "维度ID", required = true)
	private Long dimensionId;

	// 维度名称
	@ApiModelProperty(value = "维度名称", required = true)
	private String dimensionName;

	// 维度排序
	@ApiModelProperty(value = "维度排序", required = true)
	private Long dimensionSort;

	// 得分
	@ApiModelProperty(value = "得分", required = true)
	private Double score;

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof AppDimensionScoreDomain))
			try {
				throw new Exception("对象不对呀！", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		AppDimensionScoreDomain d = (AppDimensionScoreDomain) o;
		if (this.getScore() > d.getScore()) {
			return -1;
		} else if (d.getScore() - this.getScore() < 0.0001) {
			if (this.getDimensionSort() > d.getDimensionSort()) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return 1;
		}
	}

}
