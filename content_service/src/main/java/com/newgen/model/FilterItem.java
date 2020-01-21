package com.newgen.model;

import java.util.ArrayList;
import java.util.List;

public class FilterItem {
	String id;
	String value;
	Integer count;
	boolean checked;
	List<FilterItem> fields;
	
	public FilterItem() {
		this.fields = new ArrayList<FilterItem>();
	}
	
	public FilterItem(String id, String value, Integer count) {
		super();
		this.id = id;
		this.value = value;
		this.count = count;
		this.fields = new ArrayList<FilterItem>();
	}
	
	public FilterItem(String id, String value, Integer count, List<FilterItem> fields) {
		super();
		this.id = id;
		this.value = value;
		this.count = count;
		this.fields = fields;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}

	public List<FilterItem> getFields() {
		return fields;
	}

	public void setFields(List<FilterItem> fields) {
		this.fields = fields;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean check) {
		this.checked = check;
	}
	
}
