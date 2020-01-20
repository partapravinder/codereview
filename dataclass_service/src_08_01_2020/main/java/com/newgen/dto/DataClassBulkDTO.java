package com.newgen.dto;

import java.util.List;

import com.newgen.model.DataClass;

public class DataClassBulkDTO {
	private List<DataClass> dataClasses;

	public DataClassBulkDTO() {
		super();
	}

	public DataClassBulkDTO(List<DataClass> dataClasses) {
		this.dataClasses = dataClasses;
	}

	public List<DataClass> getDataClasses() {
		return dataClasses;
	}

	public void setDataClasses(List<DataClass> dataClasses) {
		this.dataClasses = dataClasses;
	}

}
