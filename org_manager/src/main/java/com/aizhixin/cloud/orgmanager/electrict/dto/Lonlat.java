package com.aizhixin.cloud.orgmanager.electrict.dto;

import io.swagger.annotations.ApiModelProperty;

public class Lonlat {

	    @ApiModelProperty(value = "经度", required = true)
	    private String           longitude;


	    @ApiModelProperty(value = "纬度", required = true)
	    private String            latitude;


		public String getLongitude() {
			return longitude;
		}


		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}


		public String getLatitude() {
			return latitude;
		}


		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}


		public Lonlat(String longitude, String latitude) {
			super();
			this.longitude = longitude;
			this.latitude = latitude;
		}


		public Lonlat() {
			super();
			// TODO Auto-generated constructor stub
		}


}
