package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public interface PaginationSQL {

	  public String getFindCountSql();

	  public String getFindSql();

	  public List<SortDTO> sort();

}