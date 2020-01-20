package com.newgen.model;

import java.util.HashMap;

public class FilterItemCache {
	String id;
	String value;
	Integer count;
	HashMap<String, FilterItemCache> subItems;
	
	public FilterItemCache() {
		super();
		id = "";
		value = "";
		count = 0;
		subItems = new HashMap<String, FilterItemCache>();
	}

	public FilterItemCache(String id, String value, Integer count, HashMap<String, FilterItemCache> subItems) {
		super();
		this.id = id;
		this.value = value;
		this.count = count;
		this.subItems = subItems;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public HashMap<String, FilterItemCache> getSubItems() {
		return subItems;
	}
	
	public void setSubItems(HashMap<String, FilterItemCache> subItems) {
		this.subItems = subItems;
	}
}
