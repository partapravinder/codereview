package com.newgen.dto;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;

public class DataFieldDTO {
	@Id
	private String id; // dataFieldIndex not to be updated
	private String dataFieldIndex;
	@NotEmpty(message = "dataFieldIndexName must not be blank!")
	private String dataFieldIndexName;
	private String dataFieldIndexType;
	private String dataFieldIndexLength;
	private String usefulInfoFlag;
	private String usefulInfoSize;
	private String indexAttribute;
	private String indexFlag;
	private String pickable;
	private String fieldOrder;

	public DataFieldDTO(String dataFieldIndex) {
		super();
		this.dataFieldIndex = dataFieldIndex;
	}

	public DataFieldDTO() {
		super();
	}

	public DataFieldDTO(String dataFieldIndex, String dataFieldIndexName, String dataFieldIndexType,
			String dataFieldIndexLength, String usefulInfoFlag, String usefulInfoSize, String indexAttribute,
			String pickable, String fieldOrder) {
		super();
		this.dataFieldIndex = dataFieldIndex;
		this.dataFieldIndexName = dataFieldIndexName;
		this.dataFieldIndexType = dataFieldIndexType;
		this.dataFieldIndexLength = dataFieldIndexLength;
		this.usefulInfoFlag = usefulInfoFlag;
		this.usefulInfoSize = usefulInfoSize;
		this.indexAttribute = indexAttribute;
		this.pickable = pickable;
		this.fieldOrder = fieldOrder;
	}

	public String getDataFieldIndex() {
		return dataFieldIndex;
	}

	public void setDataFieldIndex(String dataFieldIndex) {
		this.dataFieldIndex = dataFieldIndex;
	}

	public String getUsefulInfoFlag() {
		return usefulInfoFlag;
	}

	public void setUsefulInfoFlag(String usefulInfoFlag) {
		this.usefulInfoFlag = usefulInfoFlag;
	}

	public String getUsefulInfoSize() {
		return usefulInfoSize;
	}

	public void setUsefulInfoSize(String usefulInfoSize) {
		this.usefulInfoSize = usefulInfoSize;
	}

	public String getDataFieldIndexName() {
		return dataFieldIndexName;
	}

	public void setDataFieldIndexName(String dataFieldIndexName) {
		this.dataFieldIndexName = dataFieldIndexName;
	}

	public String getDataFieldIndexType() {
		return dataFieldIndexType;
	}

	public void setDataFieldIndexType(String dataFieldIndexType) {
		this.dataFieldIndexType = dataFieldIndexType;
	}

	public String getDataFieldIndexLength() {
		return dataFieldIndexLength;
	}

	public void setDataFieldIndexLength(String dataFieldIndexLength) {
		this.dataFieldIndexLength = dataFieldIndexLength;
	}

	public String getIndexAttribute() {
		return indexAttribute;
	}

	public void setIndexAttribute(String indexAttribute) {
		this.indexAttribute = indexAttribute;
	}

	public String getIndexFlag() {
		return indexFlag;
	}

	public void setIndexFlag(String indexFlag) {
		this.indexFlag = indexFlag;
	}

	public String getPickable() {
		return pickable;
	}

	public void setPickable(String pickable) {
		this.pickable = pickable;
	}

	public String getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(String fieldOrder) {
		this.fieldOrder = fieldOrder;
	}

}
