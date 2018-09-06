package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "PraDimensionScoreDomain", description = "维度得分结构体")
@Data
public class PraDimensionScoreDomain implements Comparable<Object> {

	// 维度ID
	@ApiModelProperty(value = "维度ID", required = true)
	private Long dimensionId;

	// 维度名称
	@ApiModelProperty(value = "维度名称", required = true)
	private String dimensionName;

	// 维度描述
	@ApiModelProperty(value = "维度描述", required = true)
	private String dimensionDescription;

	// 得分
	@ApiModelProperty(value = "得分", required = true)
	private Double score;

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof PraDimensionScoreDomain))
			try {
				throw new Exception("对象不对呀！", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		PraDimensionScoreDomain d = (PraDimensionScoreDomain) o;
		if (this.getScore() > d.getScore()) {
			return -1;
		}

		if (this.getScore() < d.getScore()) {
			return 1;
		}
		if (this.getScore() == d.getScore()) {
			return 0;
		}
		return -1;
	}

}
