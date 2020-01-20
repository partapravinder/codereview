package com.newgen.model;

import java.util.List;

public class InOutParameters {

	private Double requestPayloadSize;
	private Double responsePayloadSize;
	DataClass dataclass;
	List<DataClass> dataClassList;

	public Double getRequestPayloadSize() {
		return requestPayloadSize;
	}

	public void setRequestPayloadSize(Double requestPayloadSize) {
		this.requestPayloadSize = requestPayloadSize;
	}

	public Double getResponsePayloadSize() {
		return responsePayloadSize;
	}

	public void setResponsePayloadSize(Double responsePayloadSize) {
		this.responsePayloadSize = responsePayloadSize;
	}

	public DataClass getDataclass() {
		return dataclass;
	}

	public void setDataclass(DataClass dataclass) {
		this.dataclass = dataclass;
	}

	public List<DataClass> getDataClassList() {
		return dataClassList;
	}

	public void setDataClassList(List<DataClass> dataClassList) {
		this.dataClassList = dataClassList;
	}

}
