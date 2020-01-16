package com.newgen.dto;

import java.util.Map;
import java.util.Set;

public class FTSSearchDTO {
	Map<String, Set<Object>> params;

	public Map<String, Set<Object>> getParams() {
		return params;
	}

	public void setParams(Map<String, Set<Object>> params) {
		this.params = params;
	}
}
