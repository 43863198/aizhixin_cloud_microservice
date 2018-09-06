package com.aizhixin.cloud.ew.common.jdbc;

import java.util.List;

public interface PaginationSQL {

	public String getFindCountSql();

	public String getFindSql();

	public List<SortDTO> sort();
}
